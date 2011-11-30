/*
 * (C) Francisco Perez-Sorrosal
 
 * This file is part of the Chat-Akka project.
 
 * Chat-Akka is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 
 * Chat-Akka is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 
 * You can find a copy of the GNU General Public License
 * at <http://www.gnu.org/licenses/>.
 */

package com.linkingenius.chat

import akka.actor.Actor
import akka.actor.Actor._
import akka.actor.ActorRef
import akka.actor.ReceiveTimeout
import scala.collection.immutable.HashMap
import com.linkingenius.chat.utils.Logging
import akka.actor.TypedActor

case class User(name: String) {
  override def toString = name
}
case class Message(text: String)

case class Register(user: User)
case class Deregister(user: User)
case class UserMessage(user: User, text: Message)

class ChatRoom(roomName: String, chatService: RegistrationService) extends Actor with Logging {

  class Session(user: User, originRoom: Option[ActorRef]) extends Actor {

    self.receiveTimeout = Some(6000000L)

    def receive = {
      case UserMessage(sender, msg) => println("[ " + user.toString + "#" + roomName + " ] " + sender + " says: " + msg)

      case ReceiveTimeout =>
        logger.debug("Timeout!")
        originRoom ! Deregister(user)
        self.exit()
    }
  }

  var session = HashMap.empty[User, ActorRef]

  def receive = {
    case Register(user) =>
      val sessionUser = actorOf(new Session(user, optionSelf)).start()
      session += (user -> sessionUser)
      logger.debug(user + " subscribed")
    case Deregister(user) =>
      session -= (user)
      logger.debug(user + " unsubscribed")
      if (session.size == 0) {
        logger.debug("Room empty")
        chatService.destroyRoom(roomName)
        self.stop()
      }
    case UserMessage(user, post) =>
      for (key <- session.keys; if key != user) { session(key).forward(UserMessage(user, post)) }
    case _ => logger.debug("Unknown message")
  }

}

trait RegistrationService {
  def register(user: User, roomName: String): ActorRef
  def deregister(user: User)
  def destroyRoom(name: String)
}

object SimpleChat extends TypedActor with RegistrationService with Logging {
  type RoomKey = String
  type Room = ActorRef

  var rooms = HashMap.empty[RoomKey, Room]
  var usersRooms = HashMap.empty[User, Room]

  override def preStart() = logger.debug("Running")

  private def createRoom(name: RoomKey): Room = {
    logger.debug("Creating room " + name)
    val room = actorOf(new ChatRoom(name, this)).start()
    rooms += (name -> room)
    room
  }

  def destroyRoom(name: RoomKey) = {
    logger.debug("Destroying room " + name)
    rooms -= (name)
  }

  def register(user: User, roomName: RoomKey): ActorRef = {
    val room = rooms.getOrElse(roomName, createRoom(roomName))
    room ! Register(user)
    usersRooms += (user -> room)
    room
  }

  def deregister(user: User) = {
    val userRoom = usersRooms.get(user)
    userRoom ! Deregister(user)
    usersRooms -= (user)
  }

}
