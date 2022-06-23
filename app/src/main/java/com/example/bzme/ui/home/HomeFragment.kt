package com.example.bzme.ui.home

import android.R
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.*
import android.widget.TableLayout
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.example.bzme.DB.DBHelper
import com.example.bzme.databinding.FragmentHomeBinding
import com.example.bzme.Helper.Data as HelperData
import java.text.SimpleDateFormat
import java.util.*

open class HomeFragment() : Fragment() {
    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null
    private var helperData = HelperData()
    // This property is only valid between onCreateView and
    private val binding get() = _binding!!
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val textView: TextView = binding.textHome

        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        this.context?.let { setTableRow(it) }
        return root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setTableRow(context: Context){
        var tl : TableLayout = binding.activities
        var db = DBHelper(context)

        var schedules = db.listAllActivities()
        for(i in 0..schedules.size -1){
            val row = TableRow(this.context)
            val tv = TextView(this.context)
            val delBtn = ImageButton(this.context)
            val editbtn = ImageButton(this.context)
            val date = helperData.convertDateFromDb(schedules[i].created_at)
            val fromTime = helperData.convertDateTimeToTime(schedules[i].from_time)
            val toTime = helperData.convertDateTimeToTime(schedules[i].to_time);
            var dateAndTime = "$date $fromTime - $toTime"
            var activityId = schedules[i].id
            row.layoutParams = TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT,
            );

            delBtn.setBackgroundResource(com.example.bzme.R.drawable.ic_delete_activity)
            editbtn.setBackgroundResource(com.example.bzme.R.drawable.ic_baseline_edit_24)

            delBtn.setOnClickListener {
                db.deleteActivity(schedules[i].id)
                refreshFragment()
            }

            editbtn.setOnClickListener{
                // Initialize Dialog
                val dialog = Dialog(context)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setCancelable(true)
                dialog.setContentView(com.example.bzme.R.layout.edit_fragment)
                dialog.window?.attributes?.width = ViewGroup.LayoutParams.MATCH_PARENT

                // Populate Fields
                // Get Fields from dialog
                var hiddenId = dialog.findViewById<TextView>(com.example.bzme.R.id.hidden_id)
                var title = dialog.findViewById<EditText>(com.example.bzme.R.id.title)
                var create_date = dialog.findViewById<EditText>(com.example.bzme.R.id.create_date)
                var fromTime2 = dialog.findViewById<EditText>(com.example.bzme.R.id.from_time)
                var toTime2 = dialog.findViewById<EditText>(com.example.bzme.R.id.to_time)
                var reply = dialog.findViewById<EditText>(com.example.bzme.R.id.reply)
                var cancelBtn = dialog.findViewById<Button>(com.example.bzme.R.id.cancel)
                var submit = dialog.findViewById<Button>(com.example.bzme.R.id.submit_edit)

                // Display Date picker
                create_date.setOnClickListener{
                    val dat = Calendar.getInstance()
                    val dateSetListener = DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->
                        dat.set(Calendar.MONTH,month)
                        dat.set(Calendar.DAY_OF_MONTH,day)
                        dat.set(Calendar.YEAR,year)
                        var date = SimpleDateFormat("MM/dd/YYYY").format(dat.time)
                        create_date.setText(date)
                    }

                    var datePicker = DatePickerDialog(context,dateSetListener,dat.get(Calendar.YEAR),
                        dat.get(Calendar.MONTH),dat.get(Calendar.DAY_OF_MONTH))
                    datePicker.getDatePicker().setMinDate(System.currentTimeMillis())

                    datePicker.show()
                }

                // Display Time Picker
                fromTime2.setOnClickListener{
                    showTimePicker(fromTime2,fromTime2,toTime2)
                }
                toTime2.setOnClickListener{
                    showTimePicker(toTime2,fromTime2,toTime2)
                }

                // Set Field Values
                hiddenId.setText("" + activityId)
                title.setText(schedules.get(i).title)
                create_date.setText(date)
                fromTime2.setText(fromTime)
                toTime2.setText(toTime)
                reply.setText(schedules.get(i).reply)

                //Close Dialog
                cancelBtn.setOnClickListener{
                    dialog.dismiss()
                }
                var saveFlag = true

                //Submit
                submit.setOnClickListener{
                    saveFlag = (title.text.toString().isNotEmpty()
                            && fromTime2.text.toString().isNotEmpty()
                            && toTime2.text.toString().isNotEmpty()
                            && reply.text.toString().isNotEmpty()
                            && create_date.text.toString().isNotEmpty()
                            && helperData.validateDate(create_date.text.toString()))

                    saveFlag = (saveFlag && helperData.validateTime(fromTime2.text.toString(), toTime2.text.toString()))

                    if(saveFlag){
                        db.updateActivity(
                            "$activityId",
                            title.text.toString(),
                            reply.text.toString(),
                            toTime2.text.toString(),
                            fromTime2.text.toString(),
                            create_date.text.toString())

                        // Refresh Fragment To Reload Data
                        refreshFragment()

                        // Close Dialog
                        dialog.dismiss()
                    }
                }

                dialog.show()
            }

            tv.width = 720

            tv.setPadding(0,0,0,0)
            tv.text = schedules.get(i).title + "\n" + dateAndTime
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17f)

            tl.addView(row)
//            row.setBackgroundResource(com.example.bzme.R.drawable.underline)
            row.setPadding(30,30,0,0)
            row.addView(tv)
            row.addView(editbtn)
            row.addView(delBtn)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showTimePicker(timeInput: EditText, fromTime: EditText, toTime: EditText){
        val cal = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)

            timeInput.setText( SimpleDateFormat("HH:mm").format(cal.time))
        }

        TimePickerDialog(
            context,
            timeSetListener,
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            false
        ).show()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun refreshFragment(){
        findNavController().navigate(
            com.example.bzme.R.id.navigation_home,
            null,
            navOptions { // Use the Kotlin DSL for building NavOptions
                anim {
                    enter = R.animator.fade_in
                    exit = R.animator.fade_out
                }
            }
        )
    }
}
