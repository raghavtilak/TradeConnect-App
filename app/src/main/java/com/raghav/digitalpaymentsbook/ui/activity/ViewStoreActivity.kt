package com.raghav.digitalpaymentsbook.ui.activity

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.raghav.digitalpaymentsbook.adapter.StoreItemAdapter
import com.raghav.digitalpaymentsbook.data.network.RetrofitHelper
import com.raghav.digitalpaymentsbook.databinding.ActivityStoreBinding
import com.raghav.digitalpaymentsbook.ui.dialog.LoadingDialog
import com.raghav.digitalpaymentsbook.ui.fragment.BatchsDetailContainerFragment
import kotlinx.coroutines.launch
import org.apache.poi.ss.usermodel.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream

class ViewStoreActivity : AppCompatActivity() {
    lateinit var binding: ActivityStoreBinding

    val loadingDialog  = LoadingDialog()

    val launcher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        if (it != null)
            convertExcelToJson(it)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addProducts.setOnClickListener {
            launcher.launch("text/xml")
        }
        val adapter = StoreItemAdapter{

            loadingDialog.show(supportFragmentManager,"loading")

            lifecycleScope.launch {
                val res = RetrofitHelper.getInstance(this@ViewStoreActivity).getBatchesById(it.batchIds)
                if(res.isSuccessful && res.body()!=null){
                    loadingDialog.dismiss()
                    val frag = BatchsDetailContainerFragment()
                    val bundle = Bundle()
                    bundle.putParcelableArrayList("batches",ArrayList(res.body()!!))
                    frag.arguments = bundle
                    frag.show(supportFragmentManager,"batchDetails")
                }else{
                    loadingDialog.dismiss()
                    Toast.makeText(
                        this@ViewStoreActivity,
                        "Batch details couldn't be load",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL,false)
        binding.recyclerView.adapter = adapter

        lifecycleScope.launch {

            loadingDialog.show(supportFragmentManager,"loading")

            val result =
                RetrofitHelper.getInstance(this@ViewStoreActivity).myStore()
            if (result.isSuccessful && result.body() != null) {
                val list = result.body()!!
                adapter.submitList(list)
                Log.d("TAG","custo her ${result.body()}")
//                viewModel.retailerList.value = list.toMutableList()
            }else{
                Log.d("TAG","custo her ${result.body()}")
            }
        }
    }


    fun convertExcelToJson(uri: Uri): String {
        val inputStream: InputStream = contentResolver.openInputStream(uri)!!
        val workbook: Workbook = WorkbookFactory.create(inputStream)

        val sheet: Sheet = workbook.getSheetAt(0)
        val headerRow: Row = sheet.getRow(0)

        val jsonArray = JSONArray()

        for (i in 1 until sheet.physicalNumberOfRows) {
            val currentRow: Row = sheet.getRow(i)
            val jsonObject = JSONObject()

            for (j in 0 until headerRow.physicalNumberOfCells) {
                val cell: Cell = currentRow.getCell(j)
                val columnName = headerRow.getCell(j).stringCellValue
                when (cell.cellType) {
                    CellType.NUMERIC -> jsonObject.put(columnName, cell.numericCellValue)
                    CellType.STRING -> jsonObject.put(columnName, cell.stringCellValue)
                    CellType.BOOLEAN -> jsonObject.put(columnName, cell.booleanCellValue)
                    CellType.BLANK -> jsonObject.put(columnName, "")
                    else -> jsonObject.put(columnName, "")
                }
            }
            jsonArray.put(jsonObject)
        }
        return jsonArray.toString()
    }

}