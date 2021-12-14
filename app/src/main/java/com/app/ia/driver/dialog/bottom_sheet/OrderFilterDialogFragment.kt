package com.app.ia.driver.dialog.bottom_sheet

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.ia.driver.R
import com.app.ia.driver.utils.CommonUtils
import com.app.ia.driver.utils.toast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_order_filter.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class OrderFilterDialogFragment(private val selectedStartDate: String, private val selectedEndDate: String, private val selectedMinPrice: String, private val selectedMaxPrice: String) : BottomSheetDialogFragment() {

    private var onClickListener: OnProductFilterClickListener? = null

    fun setOnItemClickListener(onClickListener: OnProductFilterClickListener) {
        this.onClickListener = onClickListener
    }

    override fun getTheme(): Int {
        return R.style.BottomSheetDialogTheme
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog!!.setOnShowListener { dialog ->
            val d = dialog as BottomSheetDialog
            val bottomSheetInternal = d.findViewById<View>(R.id.design_bottom_sheet)
            BottomSheetBehavior.from(bottomSheetInternal!!).state = BottomSheetBehavior.STATE_EXPANDED
        }
        return inflater.inflate(R.layout.dialog_order_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUp()
    }

    private fun setUp() {

        edtTextStartDate.setText(selectedStartDate)
        edtTextEndDate.setText(selectedEndDate)
        edtTextMinPrice.setText(selectedMinPrice)
        edtTextMaxPrice.setText(selectedMaxPrice)

        tvCancel.setOnClickListener {
            dismiss()
        }

        edtTextStartDate.setOnClickListener { CommonUtils.openDatePicker(requireContext(), edtTextStartDate) }
        edtTextEndDate.setOnClickListener { CommonUtils.openDatePicker(requireContext(), edtTextEndDate) }
        edtTextOrderStatus.setOnClickListener { selectOrderStatusDialog() }

        buttonApplyFilter.setOnClickListener {
            val startDate = edtTextStartDate.text.toString().trim()
            val endDate = edtTextEndDate.text.toString().trim()
            val minPrice = edtTextMinPrice.text.toString().trim()
            val maxPrice = edtTextMaxPrice.text.toString().trim()
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)

            var startDate1 = Date()
            var endDate1 = Date()
            if (startDate.isNotEmpty() && endDate.isNotEmpty()) {
                startDate1 = sdf.parse(startDate)!!
                endDate1 = sdf.parse(endDate)!!
            }

            when {
                startDate.isEmpty() -> {
                    requireActivity().toast("Please enter start date.")
                }
                endDate.isEmpty() -> {
                    requireActivity().toast("Please enter end date.")
                }
                minPrice.isEmpty() -> {
                    requireActivity().toast("Please enter min price.")
                }
                maxPrice.isEmpty() -> {
                    requireActivity().toast("Please enter max price.")
                }
                startDate1.after(endDate1) -> {
                    requireActivity().toast("Start date should be less than end date.")
                }
                minPrice.toDouble() > maxPrice.toDouble() -> {
                    requireActivity().toast("Min price should be less than max price.")
                }
                else -> {
                    onClickListener!!.onSubmitClick(startDate, endDate, minPrice, maxPrice)
                    dismiss()
                }
            }
        }
    }

    interface OnProductFilterClickListener : OrderSortByDialogFragment.OnOrderSortByClickListener {
        fun onSubmitClick(filterValue: String, endDate: String, minPrice: String, maxPrice: String)
    }

    private fun selectOrderStatusDialog() {
        val orderStatusList = ArrayList<String>()
        orderStatusList.add(getString(R.string.status_delivered))
        orderStatusList.add(getString(R.string.status_not_delivered))
        val orderStatusArr = arrayOfNulls<CharSequence>(orderStatusList.size)
        for (i in orderStatusList.indices) {
            orderStatusArr[i] = orderStatusList[i]
        }
        val dialogBuilder = android.app.AlertDialog.Builder(requireActivity())
        dialogBuilder.setTitle(getString(R.string.select_order_status))
        dialogBuilder.setItems(
            orderStatusArr
        ) { dialog, item ->
            edtTextOrderStatus.setText(orderStatusList[item])
            //selectedProviderId.value = providerListData.value!![item].id
        }
        //Create alert dialog object via builder
        val alertDialogObject = dialogBuilder.create()
        //Show the dialog
        alertDialogObject.show()
    }

}