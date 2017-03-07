/**
  * Created by Neil on 2/27/2017.
  */
object RegionUtils {
  def hash(reg:bwta.Region) : Int = {
    return reg.getCenter.getX
  }
}
