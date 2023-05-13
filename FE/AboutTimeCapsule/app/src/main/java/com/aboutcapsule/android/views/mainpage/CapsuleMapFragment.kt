package com.aboutcapsule.android.views.mainpage

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.aboutcapsule.android.R
import com.aboutcapsule.android.data.capsule.MapInfoDto
import com.aboutcapsule.android.databinding.FragmentCapsuleMapBinding
import com.aboutcapsule.android.factory.CapsuleViewModelFactory
import com.aboutcapsule.android.model.CapsuleViewModel
import com.aboutcapsule.android.repository.CapsuleRepo
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class CapsuleMapFragment : Fragment() , OnMapReadyCallback ,
    MainPageMyCapsuleFragment.DataPassListner {
    private val TAG = "CapsuleMapFragment"
    companion object {
        lateinit var binding: FragmentCapsuleMapBinding
        lateinit var navController: NavController
        private lateinit var mMap : GoogleMap
        lateinit var markerOptions: MarkerOptions

        private lateinit var findHost : String // 분기처리 정보 넘겨받음 , api 판별용
        private lateinit var viewModel : CapsuleViewModel

        lateinit var mapMarkers : MutableList<MapInfoDto>
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        callingApi() // api 받아오기

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_capsule_map,container,false)


        binding.mainpageMapFragment.onCreate(savedInstanceState)
        binding.mainpageMapFragment.getMapAsync(this)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setNavigation()


//        findHost =  MainActivity.preferences.getString("meOrFriend","notKey")
//        Log.d("캡슐맵프래그먼트" , findHost)

    }

    private fun setNavigation(){
        val navHostFragment =requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
    }

    // 뷰페이저 페이지에서 넘어온 데이터 받아서 api 통신 ( 나 , 친구 분기처리 )
    override fun onDataPass(data: String) {
        findHost = data
    }

    // onDataPass로 MainPageMyCapsuleFragment에서 넘겨받은 분기처리 정보 받은 후 api 통신
    private fun callingApi(){

        val repository = CapsuleRepo()
        val capsuleViewModelFactory = CapsuleViewModelFactory(repository)
        viewModel = ViewModelProvider  (this, capsuleViewModelFactory).get(CapsuleViewModel::class.java)

        Log.d(TAG,"맵 $findHost")
        when(findHost){
            "myCapsuleApi" -> {
                Log.d("api", " 내 캡슐  ")
                viewModel.getMyCapsuleList(1)
                viewModel.myCapsuleList.observe(viewLifecycleOwner){
                    Log.d(TAG,"${it.mapInfoDtoList}")
                    mapMarkers = it.mapInfoDtoList
                }
            }
            "friendApi" -> {
                Log.d("api", " 친구 캡슐  ")
                viewModel.getFriendCapsuleList(1)
                viewModel.friendCapsuleList.observe(viewLifecycleOwner){
                    Log.d(TAG,"${it.mapInfoDtoList}")
                    mapMarkers = it.mapInfoDtoList
                }
            }
        }
    }

    // 지도 띄워주기
    // onCreateView에서 getMapAsync(this) 사용허가를 구하면 안드로이드가 메서드 실행
    override fun onMapReady(map: GoogleMap) {
       mMap = map

        val defalutPos = LatLng(35.894332,127.9)

        for(i in 0 until mapMarkers.size ) {

            var currLatLng = LatLng(mapMarkers[i].latitude,mapMarkers[i].longitude)
            var capsuleId = mapMarkers[i].capsuleId
            var isLock = mapMarkers[i].isLocked
            var isOpen = mapMarkers[i].isOpened

            markerOptions = MarkerOptions()
            setCustomOpenMarker(isOpen,isLock)
           val marker = mMap.addMarker(markerOptions.position(currLatLng))
            marker?.tag = capsuleId
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defalutPos,6.8f))
    }

    fun setCustomOpenMarker(openFlag : Boolean , lockFlag : Boolean){

        if(lockFlag) {
            var bitmapdraw: BitmapDrawable =
                resources.getDrawable(R.drawable.locked_marker) as BitmapDrawable
            var bitmap = bitmapdraw.bitmap
            var customMarker = Bitmap.createScaledBitmap(bitmap, 90, 120, false)
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(customMarker))
        }else if(openFlag){
            var bitmapdraw: BitmapDrawable =
                resources.getDrawable(R.drawable.mine_marker) as BitmapDrawable
            var bitmap = bitmapdraw.bitmap
            var customMarker = Bitmap.createScaledBitmap(bitmap, 90, 120, false)
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(customMarker))
        }
    }


    override fun onStart() {
        super.onStart()
        binding.mainpageMapFragment.onStart()
    }

    override fun onStop() {
        super.onStop()
        binding.mainpageMapFragment.onStop()
    }

    override fun onResume() {
        super.onResume()
        binding.mainpageMapFragment.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mainpageMapFragment.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mainpageMapFragment.onLowMemory()
    }

    override fun onDestroy() {
      binding.mainpageMapFragment.onDestroy()
        super.onDestroy()
    }

}