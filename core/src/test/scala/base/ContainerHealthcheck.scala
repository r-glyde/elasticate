package base

import com.spotify.docker.client.{DefaultDockerClient, DockerClient}

trait ContainerHealthcheck {

  val docker: DockerClient = DefaultDockerClient.fromEnv().build()

  def containerReady(containerId: String, cmd: String, condition: String => Boolean, tries: Int = 60): Boolean = {
    def healthcheck: String = {
      val execId = docker.execCreate(
        containerId,
        List("sh", "-c", cmd).toArray,
        DockerClient.ExecCreateParam.attachStdin(),
        DockerClient.ExecCreateParam.attachStdout()
      )
      docker.execStart(execId.id()).readFully()
    }

    def loop(current: Int): Boolean =
      if (current == tries) false
      else if (condition(healthcheck)) true
      else {
        Thread.sleep(1000)
        loop(current + 1)
      }

    loop(0)
  }

}
