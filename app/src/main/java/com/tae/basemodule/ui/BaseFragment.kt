package com.base.taelib.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.base.taelib.viewmodel.BaseViewModel

abstract class BaseFragment<T : ViewDataBinding, R : BaseViewModel>
    (@LayoutRes private val layoutId: Int) : Fragment() {

    abstract fun initData()

    abstract fun initView()
    abstract fun eventObservers()
    lateinit var binding: T
    abstract val viewModel: R

    private var mToast: Toast? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        binding.lifecycleOwner = this
        binding.setVariable(layoutId, viewModel)
        binding.executePendingBindings()
        initData()
        initView()
        eventObservers()
        return binding.root
    }

    /**
     * toast one time
     * @param[msg] message
     **/
    fun showToast(msg: String) {
        mToast?.cancel()
        mToast = Toast.makeText(requireActivity(), msg, Toast.LENGTH_SHORT)
        mToast?.show()
    }

}