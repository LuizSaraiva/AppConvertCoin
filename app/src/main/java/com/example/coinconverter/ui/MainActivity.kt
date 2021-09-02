package com.example.coinconverter.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.core.widget.doAfterTextChanged
import com.example.coinconverter.R
import com.example.coinconverter.core.*
import com.example.coinconverter.core.extensions.createDialog
import com.example.coinconverter.core.extensions.createProgressDialog
import com.example.coinconverter.data.model.Coin
import com.example.coinconverter.databinding.ActivityMainBinding
import com.example.coinconverter.presentation.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val dialog by lazy { createProgressDialog() }
    private val viewModel by viewModel<MainViewModel>()
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        bindListeners()
        bindAdapters()
        bindObserve()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.action_history){
            startActivity(Intent(this, HistoryActivity::class.java ))
        }
        return super.onOptionsItemSelected(item)
    }

    private fun bindListeners() {

        binding.tilValue.editText?.doAfterTextChanged {
            binding.btnConverter.isEnabled = it != null && it.toString().isNotEmpty()
            binding.btnSalvar.isEnabled = false
        }

        binding.btnConverter.setOnClickListener {

            it.hideSoftKeyboard()

            val search = "${binding.tilFrom.text}-${binding.tilTo.text}"
            viewModel.getExchangeValue(search)
        }

        binding.btnSalvar.setOnClickListener {
            val value = viewModel.state.value
            (value as? MainViewModel.State.Success)?.let {
                viewModel.saveExchange(it.exchange)
            }
        }

    }

    private fun bindAdapters() {
        val list = Coin.values()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)

        binding.tvFrom.setAdapter(adapter)
        binding.tvTo.setAdapter(adapter)

        binding.tvFrom.setText(Coin.BRL.name, false)
        binding.tvTo.setText(Coin.USD.name, false)
    }

    private fun bindObserve() {
        viewModel.state.observe(this) {
            when(it) {
                is MainViewModel.State.Error -> {
                    dialog.dismiss()
                    createDialog {
                        setMessage(it.error.message)
                    }.show()
                }
                MainViewModel.State.Loading -> dialog.show()
                is MainViewModel.State.Success -> Success(it)
                MainViewModel.State.Saved -> {
                    dialog.dismiss()
                    createDialog {
                        setMessage("Item salvo com sucesso!")
                    }.show()
                }
            }
        }

    }

    private fun Success(it: MainViewModel.State.Success) {
        dialog.dismiss()

        binding.btnSalvar.isEnabled = true

        val selectedCoin = binding.tilTo.text
        val coin = Coin.values().find { it.name == selectedCoin } ?: Coin.BRL


        val result = it.exchange.bid * binding.tilValue.text.toDouble()

        binding.tvResult.text = result.formatCurrency(coin.locale)
    }

}