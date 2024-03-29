package com.example.what_s_in_my_luggage

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResultListener
import com.example.what_s_in_my_luggage.model.Luggage

class AddCarrierFragment : Fragment() {
    var packingFrameActivity: PackingFrameActivity? = null

    private lateinit var btnDepartureCal: Button
    private lateinit var btnArrivalCal: Button
    private lateinit var travelPlace: CardView
    private lateinit var template: CardView
    private lateinit var carrierName: EditText
    private lateinit var txtTravelPlace: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Fragment Result API
        setFragmentResultListener("selectDate") { key, bundle ->
            val date = bundle.getString("date")
            val button = bundle.getInt("button")
            setDate(button, date!!)
        }
        setFragmentResultListener("travelPlace") { key, bundle ->
            val place = bundle.getString("dialogListView")
            setTravelPlace(place!!)
        }
        setFragmentResultListener("template") { key, bundle ->
            val template = bundle.getString("dialogListView")
            setTemplate(template!!)
        }

        // data 초기화
        var dataManager = UserDataManager.getInstance(requireContext())
        dataManager.setTravelPlaceList()
        dataManager.setSavedTemplateList()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // fragment_add_carrier.xml과 연결하여 return 함
        var view = inflater.inflate(R.layout.fragment_add_carrier, container, false)

        // 초기화
        btnDepartureCal = view.findViewById<Button>(R.id.btnDepartureCal)
        btnArrivalCal = view.findViewById<Button>(R.id.btnArrivalCal)
        travelPlace = view.findViewById<CardView>(R.id.travelPlace)
        template = view.findViewById<CardView>(R.id.template)
        carrierName = view.findViewById<EditText>(R.id.carrierName)
        txtTravelPlace = travelPlace.findViewById<TextView>(R.id.txtTravelPlace)

        // 가는 날, 오는 날 -> DialogCalFragment
        btnDepartureCal.setOnClickListener {
            showDatePickerDialog(btnDepartureCal.id)
        }
        btnArrivalCal.setOnClickListener {
            showDatePickerDialog(btnArrivalCal.id)
        }

        // travel place, template -> DialogListViewFragment
        travelPlace.setOnClickListener {
            showDialogListView("travelPlace")
        }
        template.setOnClickListener {
            showDialogListView("template")
        }

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is PackingFrameActivity) {
            packingFrameActivity = context
        }
    }

    fun showDatePickerDialog(viewid: Int) {
        // 클릭한 버튼이 무엇인지 dialogCalendar에 전달
        val dialogCalendar = DialogCalFragment()
        val bundle = Bundle()
        bundle.putInt("button", viewid)
        dialogCalendar.arguments = bundle
        dialogCalendar.setStyle(
            DialogFragment.STYLE_NORMAL,
            R.style.RoundCornerBottomSheetDialogTheme
        )
        dialogCalendar.show(parentFragmentManager, "datePicker")
    }

    fun showDialogListView(tag: String) {
        val dialogTemplate = DialogListViewFragment()
        val bundle = Bundle()
        bundle.putString("tag", tag)
        dialogTemplate.arguments = bundle
        dialogTemplate.setStyle(
            DialogFragment.STYLE_NORMAL,
            R.style.RoundCornerBottomSheetDialogTheme
        )
        dialogTemplate.show(parentFragmentManager, "template")
    }

    fun setDate(viewid: Int, date: String) {
        // 클릭한 버튼에 날짜를 설정
        when (viewid) {
            R.id.btnDepartureCal -> {
                btnDepartureCal.text = date
                btnDepartureCal.setTextColor(Color.BLACK)
            }

            R.id.btnArrivalCal -> {
                btnArrivalCal.text = date
                btnArrivalCal.setTextColor(Color.BLACK)
            }
        }
    }

    fun setTravelPlace(place: String) {
        val txtTravelPlace = travelPlace.findViewById<TextView>(R.id.txtTravelPlace)
        txtTravelPlace.text = place
        txtTravelPlace.setTextColor(Color.BLACK)
    }

    fun setTemplate(temp: String) {
        var temp = temp
        val txtTemplate = template.findViewById<TextView>(R.id.txtTemplate)
        if (temp == "")
            temp = "기본 템플릿"
        txtTemplate.text = temp
        txtTemplate.setTextColor(Color.BLACK)
    }

    fun saveTempLuggage() {
        var userName = UserDataManager.getInstance(requireContext()).getUserName()
        var carriername = carrierName.text.toString()
        var destination = txtTravelPlace.text.toString()

        var title = ""
        var content = ""

        var schedule = btnDepartureCal.text.toString() + "\n  -\n" + btnArrivalCal.text.toString()

        // imageURL 파라미터에 null을 전달하여 객체 생성
        var tempLuggage = Luggage(
            "temp",
            userName,
            carriername,
            destination,
            schedule,
            title,
            content,
            null
        ) // imageURL에 null 추가
        UserDataManager.getInstance(requireContext()).setTempLuggage(tempLuggage)
    }
}