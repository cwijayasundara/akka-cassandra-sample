import com.datastax.driver.core.{Cluster, ProtocolOptions, Session}
import org.specs2.specification.{Fragments, SpecificationStructure, Step}
import java.io.File

import akka.actor.ActorSystem
import com.cham.cassandrautil.CassandraCluster

import scala.io.Source

trait TestCassandraCluster extends CassandraCluster {

  def system: ActorSystem

  private def config = system.settings.config

  import scala.collection.JavaConversions._
  private val cassandraConfig = config.getConfig("akka-cassandra.test.db.cassandra")
  private val port = cassandraConfig.getInt("port")
  private val hosts = cassandraConfig.getStringList("hosts").toList

  lazy val cluster: Cluster =
    Cluster.builder().
      addContactPoints(hosts: _*).
      withCompression(ProtocolOptions.Compression.SNAPPY).
      withPort(port).
      build()

}

trait CleanCassandra extends SpecificationStructure {
  this: CassandraCluster =>

  private def runCql(session: Session, file: File): Unit = {
    val query = Source.fromFile(file).mkString
    query.split(";").foreach(session.execute)
  }

  import com.cham.core.Keyspaces

  private def runAllCqls(): Unit = {
    val session = cluster.connect(Keyspaces.webshop)
    val uri = getClass.getResource("/").toURI
    new File(uri).listFiles().foreach { file =>
      if (file.getName.endsWith(".cql")) runCql(session, file)
    }
    session.close()
  }

  override def map(fs: => Fragments) = super.map(fs) insert Step(runAllCqls())
}
