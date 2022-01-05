package com.base.taelib


import android.os.Bundle
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.base.taelib.viewmodel.BaseViewModel

abstract class BaseActivity<T : ViewDataBinding, R : BaseViewModel>(@LayoutRes val layoutId: Int) :
    AppCompatActivity() {

    abstract fun initData()
    abstract fun initView()
    abstract fun eventObservers()
    lateinit var binding: T
    abstract val viewModel: R

    private var mToast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutId)
        binding.lifecycleOwner = this
        binding.setVariable(layoutId, viewModel)
        binding.executePendingBindings()
        initData()
        initView()
        eventObservers()
    }

    /**
     * toast one time
     * @param[msg] message
     **/
    fun showToast(msg: String) {
        mToast?.cancel()
        mToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT)
        mToast?.show()
    }

}