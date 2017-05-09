package shine.st.blog

import com.typesafe.config.ConfigFactory
import org.joda.time.DateTime

/**
  * Created by shinest on 23/01/2017.
  */
package object api {
  val config = ConfigFactory.load()
  val bucketName = config.getString("blog.bucket_name")
  val updatePeriod = config.getInt("blog.update_period")
  val tokenKey = config.getString("token_key")

  def isExpire(date: DateTime) = DateTime.now.isAfter(date.plusDays(updatePeriod).getMillis)

  //  def isExpire(date: DateTime) = DateTime.now.isAfter(date.plusSeconds(10).getMillis)
}
