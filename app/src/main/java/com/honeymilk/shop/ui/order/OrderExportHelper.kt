package com.honeymilk.shop.ui.order

import android.content.Context
import android.net.Uri
import com.honeymilk.shop.model.Order
import jxl.Workbook
import jxl.write.Label
import jxl.write.Number
import java.io.OutputStream

class OrderExportHelper(private val context: Context) {
    fun createExcelFile(uri: Uri, orders: List<Order>) {
        try {
            val outputStream: OutputStream? = context.contentResolver.openOutputStream(uri)
            val workbook = Workbook.createWorkbook(outputStream)
            val sheet = workbook.createSheet("Orders", 0)

            // Add headers
            sheet.addCell(Label(0, 0, "Name"))
            sheet.addCell(Label(1, 0, "Email"))
            sheet.addCell(Label(2, 0, "Quantity"))
            sheet.addCell(Label(3, 0, "Design"))
            sheet.addCell(Label(4, 0, "Color"))
            sheet.addCell(Label(5, 0, "Type"))
            sheet.addCell(Label(6, 0, "Sizes"))

            // Add data rows
            var row = 1
            for (order in orders) {
                val name = order.customer.name
                val email = order.customer.email
                val designs = order.items.map { it.design }.distinct().joinToString(", ")
                val colors = order.items.map { it.color }.distinct().joinToString(", ")
                val types = order.items.map { it.type }.distinct().joinToString(", ")
                val sizes = order.items.map { it.size }.distinct().joinToString(", ")

                for (item in order.items) {
                    sheet.addCell(Label(0, row, name))
                    sheet.addCell(Label(1, row, email))
                    sheet.addCell(Number(2, row, item.quantity.toDouble()))
                    sheet.addCell(Label(3, row, designs))
                    sheet.addCell(Label(4, row, colors))
                    sheet.addCell(Label(5, row, types))
                    sheet.addCell(Label(6, row, sizes))
                    row++
                }
            }

            workbook.write()
            workbook.close()
            outputStream?.close()
        } catch (e: Exception) {
            // Handle exceptions (e.g., show an error message to the user)
            e.printStackTrace()
        }
    }

    companion object {
        const val CREATE_FILE_REQUEST_CODE = 123
    }

}