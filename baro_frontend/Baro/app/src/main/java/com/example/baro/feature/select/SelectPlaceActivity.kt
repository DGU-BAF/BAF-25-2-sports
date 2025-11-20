package com.example.baro.feature.select

import android.R
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.baro.databinding.FragmentSelectPlacesBinding

class SelectPlaceActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_PRESELECTED = "preselected"   // ArrayList<String>
        const val EXTRA_SELECTED = "selected"         // ArrayList<String>
    }

    private lateinit var binding: FragmentSelectPlacesBinding

    // 임시 후보(나중에 서버에서 받아오면 교체)
    private val PLACES = listOf("운동장", "축구장", "야구장", "헬스장", "수영장", "테니스장")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentSelectPlacesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = ArrayAdapter(this, R.layout.simple_list_item_multiple_choice, PLACES)
        binding.lvPlaces.adapter = adapter
        binding.lvPlaces.choiceMode = ListView.CHOICE_MODE_MULTIPLE

        // 미리 선택되어 온 것 체크
        val preselected = intent.getStringArrayListExtra(EXTRA_PRESELECTED) ?: arrayListOf()
        PLACES.forEachIndexed { index, s ->
            if (preselected.contains(s)) binding.lvPlaces.setItemChecked(index, true)
        }

        // 완료
        binding.btnDone.setOnClickListener {
            val checked = arrayListOf<String>()
            for (i in PLACES.indices) {
                if (binding.lvPlaces.isItemChecked(i)) checked.add(PLACES[i])
            }
            val data = Intent().putStringArrayListExtra(EXTRA_SELECTED, checked)
            setResult(RESULT_OK, data)
            finish()
        }

        // 닫기(뒤로)
        binding.btnClose.setOnClickListener { finish() }
    }

}