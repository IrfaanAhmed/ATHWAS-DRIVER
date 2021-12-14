package com.app.ia.driver.ui.offers

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.app.ia.driver.ViewModelFactory
import com.app.ia.driver.BR
import com.app.ia.driver.R
import com.app.ia.driver.apiclient.RetrofitFactory
import com.app.ia.driver.base.BaseFragment
import com.app.ia.driver.base.BaseRepository
import com.app.ia.driver.databinding.FragmentOffersBinding
import com.app.ia.driver.ui.offers.adapter.OfferListAdapter
import kotlinx.android.synthetic.main.fragment_offers.*

class OffersFragment : BaseFragment<FragmentOffersBinding, OffersViewModel>(){

    private var mFragmentBinding: FragmentOffersBinding? = null
    private var mViewModel: OffersViewModel? = null


    override val bindingVariable: Int
        get() = BR.viewModel

    override val layoutId: Int
        get() = R.layout.fragment_offers

    override val viewModel: OffersViewModel
        get() = mViewModel!!

    companion object {
        fun newInstance(): OffersFragment {
            val args = Bundle()
            val fragment = OffersFragment()
            fragment.arguments = args
            return fragment
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        setViewModel()
        super.onCreate(savedInstanceState)
        mViewModel?.setActivityNavigator(requireActivity())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFragmentBinding = viewDataBinding!!
        mFragmentBinding?.lifecycleOwner = this
        mViewModel?.setVariable(mFragmentBinding!!)

       recViewOffer.layoutManager = GridLayoutManager(requireContext(), 3)
        val adapter = OfferListAdapter()
        recViewOffer.adapter = adapter
        var categoryList = ArrayList<String>()
        categoryList.add("Oppo")
        categoryList.add("Samsung")
        categoryList.add("Nokia")
        categoryList.add("Vivo")
        adapter.submitList(categoryList)

    }

    private fun setViewModel() {
        val factory = ViewModelFactory(OffersViewModel(BaseRepository(RetrofitFactory.getInstance(), this)))
        mViewModel = ViewModelProvider(this, factory).get(OffersViewModel::class.java)
    }

}