package com.aboutcapsule.android.views.capsule

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.aboutcapsule.android.R
import com.aboutcapsule.android.data.capsule.PostRegistCapsuleReq
import com.aboutcapsule.android.databinding.FragmentCapsuleRegistGroupBinding
import com.aboutcapsule.android.factory.CapsuleViewModelFactory
import com.aboutcapsule.android.model.CapsuleViewModel
import com.aboutcapsule.android.repository.CapsuleRepo
import com.aboutcapsule.android.util.GlobalAplication
import com.aboutcapsule.android.views.MainActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


// 캡슐 등록 버튼 눌러서 넘어오면서 좌표 가져와서 지도에 뿌려주기
class CapsuleRegistGroupFragment : Fragment() ,OnMapReadyCallback{

   companion object{
       lateinit var binding : FragmentCapsuleRegistGroupBinding
       lateinit var navController : NavController
       private var markerOptions = MarkerOptions()
       private var radioBtn: String = ""
       private var isGroup : Boolean = true
       private var lat : Double = 0.0
       private var lng : Double = 0.0

       private lateinit var address : String
       private lateinit var viewModel : CapsuleViewModel
       private lateinit var groupmemberNames : String

       private var memberId = GlobalAplication.preferences.getInt("currentUser",-1)

       private var memberNameList : ArrayList<String>? = null
       private var memberIdList : MutableList<Int>? = null

       private lateinit var mMap : GoogleMap

   }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 바텀 네비 숨기기
        bottomNavToggle(true)
        // 상단 벨 숨기기
        bellToggle(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_capsule_regist_group,container,false)

        getBundleData()

        binding.registGroupMapFragment.onCreate(savedInstanceState)
        binding.registGroupMapFragment.getMapAsync(this)

        radioBtnListner()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setNavigation()

        redirectFindFriend()

        submitDatas()

        setAddMemberView()
    }

    // 추가된 멤버들 보여주기
    private fun setAddMemberView(){
        memberNameList = arguments?.getStringArrayList("memberNameList")
        var str =""
        if(memberNameList!=null){
            for(i in 0 until memberNameList!!.size){
                str+= "@"+memberNameList!!.get(i)+" "
            }
            binding.addMemberView.text=str
            groupmemberNames=str
        }else{
            binding.addMemberView.hint="멤버를 추가해주세요"
            groupmemberNames=""
        }
    }
    private fun getBundleData(){
        // 친구 찾기 페이지 가버리면 날라가서 shared로 받음
        lat = GlobalAplication.preferences.getString("lat","-1").toDouble()
        lng = GlobalAplication.preferences.getString("lng","-1").toDouble()
        address = GlobalAplication.preferences.getString("address","null")
        Log.d("캡슐등록Frag", "lat: ${lat}, lng: ${lng}, address: ${address}")
    }

    // TODO : (그룹캡슐) 캡슐 생성버튼 클릭 시 , 캡슐생성 api 보내고 페이지 이동
    private fun submitDatas(){
        binding.capsuleRegistGruopRegistbtn.setOnClickListener {

            val title = binding.capsuleRegistGroupTitle.editableText.toString() // 제목
//            memberIdList = arguments?.getIntegerArrayList("memberIdList") // 멤버 아이디
//            val tmp =arguments?.getIntegerArrayList("memberIdList")
//            Log.d("리스트",tmp.toString())
            lat // 위도
            lng // 경도
            Log.d("Lat", "lat: ${lat}")
            Log.d("radio", "$radioBtn")
            Log.d("APi_edittext", "$title")
            Log.d("APi_address", "$address")

            if (radioBtn == "") {
                Toast.makeText(requireContext(), "공개 범위를 설정해주세요", Toast.LENGTH_SHORT).show()
            } else if(groupmemberNames==""){
                Toast.makeText(requireContext(),"그룹 멤버를 추가해주세요",Toast.LENGTH_SHORT).show()
            }else if (title.isEmpty() || title.length > 30) {
                Toast.makeText(requireContext(), "제목길이는 1~30글자로 작성 가능합니다.", Toast.LENGTH_SHORT).show()
            }else {
                val memberlist =arguments?.getIntegerArrayList("memberIdList")
                memberlist?.add(0,memberId)
                Log.d("success in radio","$radioBtn")
                val repository = CapsuleRepo()
                val capsuleViewModelFactory = CapsuleViewModelFactory(repository)
                viewModel = ViewModelProvider  (this, capsuleViewModelFactory).get(CapsuleViewModel::class.java)
                var postRegistCapsuleData = PostRegistCapsuleReq(memberlist!!,title, radioBtn, isGroup,lat,lng,address)
                viewModel.addCapsule(postRegistCapsuleData)

                Log.d("capsule","${GlobalAplication.preferences.getString("capsuleId","-1")}")
                viewModel.isCapsuleRegister.observe(viewLifecycleOwner) {
                    if (it == true) {
                        var bundle = bundleOf("capsuleTitle" to title)
                        navController.navigate(R.id.action_capsuleRegistGroupFragment_to_capsuleGroupFragment, bundle)
                    }
                }


            }
        }
    }

    private fun setNavigation(){
        val navHostFragment =requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
    }

    private fun redirectFindFriend(){
        binding.addMemberBtn.setOnClickListener {
            navController.navigate(R.id.action_capsuleRegistGroupFragment_to_capsuleFindFriendFragment)
        }
    }

    private fun radioBtnListner(){
        binding.radiogruop2type.setOnCheckedChangeListener{ _ , checkedId ->
            when(checkedId){
                R.id.radio_2type_all ->  radioBtn="ALL"
                R.id.radio_2type_group -> radioBtn="GROUP"
            }
        }
        if(radioBtn=="ALL"){ // 선택후 친구 찾으러 갔을 경우 체크 상태로 유지
            binding.radiogruop2type.check(R.id.radio_2type_all)
        }else if (radioBtn=="GROUP"){
            binding.radiogruop2type.check(R.id.radio_2type_group)
        }
    }

    // 바텀 네비 숨기기 토글
    private fun bottomNavToggle(sign : Boolean){
        val mainActivity = activity as MainActivity
        mainActivity.hideBottomNavi(sign)
    }

    // 상단바 벨 사라지게 / 페이지 전환 시 다시 생성
    private fun bellToggle(sign : Boolean){
        var bell = activity?.findViewById<ImageView>(R.id.toolbar_bell)
        if(sign) {
            bell?.visibility = View.GONE
        }else{
            bell?.visibility = View.VISIBLE
        }

    }

    // 지도 띄워주기
    // onCreateView에서 getMapAsync(this) 사용허가를 구하면 안드로이드가 메서드 실행
    override fun onMapReady(map: GoogleMap) {
        mMap = map

        markerOptions = MarkerOptions()
        setCustomMarker()

        val userLocation = LatLng(lat,lng)

        mMap.addMarker(markerOptions.position(userLocation))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,16f))
    }

    private fun setCustomMarker(){
        var bitmapdraw : BitmapDrawable = resources.getDrawable(R.drawable.mine_marker) as BitmapDrawable
        var bitmap = bitmapdraw.bitmap
        var customMarker = Bitmap.createScaledBitmap(bitmap,90,120,false)
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(customMarker))
    }


    override fun onStart() {
        super.onStart()
        binding.registGroupMapFragment.onStart()
    }

    override fun onStop() {
        super.onStop()
        binding.registGroupMapFragment.onStop()
    }

    override fun onResume() {
        super.onResume()
        binding.registGroupMapFragment.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.registGroupMapFragment.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.registGroupMapFragment.onLowMemory()
    }

    override fun onDestroy() {

        bottomNavToggle(false)

        binding.registGroupMapFragment.onDestroy()
        super.onDestroy()
    }


}