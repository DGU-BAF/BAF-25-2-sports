package com.example.baro.feature.select

import android.R
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.baro.databinding.FragmentSelectSportsBinding

class SelectSportsActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_PRESELECTED = "preselected"   // ArrayList<String>
        const val EXTRA_SELECTED = "selected"         // ArrayList<String>
    }

    private lateinit var binding: FragmentSelectSportsBinding

    // 임시 후보(나중에 서버에서 받아오면 교체)
    private val SPORTS = listOf("축구", "야구", "런닝", "농구", "배드민턴", "수영", "테니스", "클라이밍")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentSelectSportsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = ArrayAdapter(this, R.layout.simple_list_item_multiple_choice, SPORTS)
        binding.lvSports.adapter = adapter
        binding.lvSports.choiceMode = ListView.CHOICE_MODE_MULTIPLE

        // 미리 선택되어 온 것 체크
        val preselected = intent.getStringArrayListExtra(EXTRA_PRESELECTED) ?: arrayListOf()
        SPORTS.forEachIndexed { index, s ->
            if (preselected.contains(s)) binding.lvSports.setItemChecked(index, true)
        }

        // 완료
        binding.btnDone.setOnClickListener {
            val checked = arrayListOf<String>()
            for (i in SPORTS.indices) {
                if (binding.lvSports.isItemChecked(i)) checked.add(SPORTS[i])
            }
            val data = Intent().putStringArrayListExtra(EXTRA_SELECTED, checked)
            setResult(RESULT_OK, data)
            finish()
        }

        // 닫기(뒤로)
        binding.btnClose.setOnClickListener { finish() }
    }
}