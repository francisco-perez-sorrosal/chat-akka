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

import akka.actor.Actor.actorOf
import akka.actor.ActorRef
import akka.actor.Actor._
import akka.util.duration._
import akka.testkit.TestKit
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.mutable.BeforeAfter
import org.specs2.runner.JUnitRunner
import akka.actor.TypedActor

@RunWith(classOf[JUnitRunner])
class ChatSpec extends Specification with ChatContext {
  franChannel ! UserMessage(User("Fran"), Message("Hi there! I'm Fran"))
  juanChannel ! UserMessage(User("Juan"), Message("Hi there! I'm Juan"))
}

trait ChatContext extends BeforeAfter {
  val chatService = TypedActor.newInstance(classOf[RegistrationService], SimpleChat)
  println("Registering users...")
  val franChannel = chatService.register(User("Fran"), "TestRoom")
  val juanChannel = chatService.register(User("Juan"), "TestRoom")

  def before = {}

  def after = {
    println("De-Registering users...")
    chatService.deregister(User("Fran"))
    chatService.deregister(User("Juan"))
  }
}
