package com.example.coinconverter.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.coinconverter.core.extensions.createDialog
import com.example.coinconverter.core.extensions.createProgressDialog
import com.example.coinconverter.databinding.ActivityHistoryBinding
import com.example.coinconverter.presentation.HistoryViewModel
import com.example.coinconverter.ui.history.HistoryListAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class HistoryActivity : AppCompatActivity() {

    private val viewModel by viewModel<HistoryViewModel>()
    private val adapter by lazy { HistoryListAdapter() }
    private val dialog by lazy { createProgressDialog() }
    private val binding by lazy { ActivityHistoryBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.rvHistory.adapter = adapter
        binding.rvHistory.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL)
        )
        bindObserver()

        lifecycle.addObserver(viewModel)

    }

    private fun bindObserver() {
        viewModel.state.observe(this) {
            when (it) {
                is HistoryViewModel.State.Error -> {
                    dialog.dismiss()
                    createDialog {
                        setMessage(it.error.message)
                    }.show()
                }
                HistoryViewModel.State.Loading -> dialog.show()
                is HistoryViewModel.State.Success -> {
                    dialog.dismiss()
                    adapter.submitList(it.list)
                }
            }
        }
    }
}