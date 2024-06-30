package com.rpi4dojo.config

import com.typesafe.config.{Config, ConfigFactory}

object ConfigLoader {
  private val config: Config = ConfigFactory.load()

  def getConfig(path: String): String = {
    config.getString(path)
  }
}
