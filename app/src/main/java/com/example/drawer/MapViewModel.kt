import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.osmdroid.util.GeoPoint

class SharedViewModel : ViewModel() {
    val startEndPoints = MutableLiveData<Pair<GeoPoint, GeoPoint>>( Pair(org.osmdroid.util.GeoPoint(45.078, 7.676),org.osmdroid.util.GeoPoint(45.078, 7.676)))
    val startEndAddress = MutableLiveData<Pair<String, String>>(Pair("",""))

    fun setPoints(s:GeoPoint, e:GeoPoint) {
        startEndPoints.value = Pair(s, e)
    }
    fun setAddresses(s: String, e: String){
        startEndAddress.value = Pair(s, e)
    }

    fun setPoint(p: GeoPoint, sel: String){
        val a = startEndPoints.value
        val first = a?.first
        val second =a?.second

        if(sel == "s"){
            startEndPoints.value = Pair(p, second!!)
        }else{
            startEndPoints.value = Pair(first!!, p)
        }
    }
    fun setAddress(p: String, sel: String){
        val a = startEndAddress.value
        val first = a?.first.toString()
        val second =a?.second.toString()

        if(sel == "s"){
            startEndAddress.value = Pair(p, second)
        }else{
            startEndAddress.value = Pair(first, p)
        }
    }
}