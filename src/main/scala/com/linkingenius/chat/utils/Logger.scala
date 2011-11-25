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

package com.linkingenius.chat.utils

import org.slf4j.{ Logger => Slf4jLogger }
import org.slf4j.LoggerFactory

trait Logger {
  val logger: Slf4jLogger

  def debug(message: => String): Unit = if (logger.isDebugEnabled) logger.debug(message) // By name parameter to evaluate only when it is used
  def error(message: => String): Unit = if (logger.isDebugEnabled) logger.error(message)
  def warn(message: => String): Unit = if (logger.isDebugEnabled) logger.warn(message)
}

trait Logging {
  self => // This allows us to access to the class that implements the trait
  // Important to make it protected to make it a member of the class where it is mixed, not public
  protected lazy val logger: Logger = new Logger {
    override val logger = LoggerFactory.getLogger(self.getClass()) // here we are accessing the class that implements the trait
  }
}

