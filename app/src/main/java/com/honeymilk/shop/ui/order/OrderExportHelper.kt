package com.honeymilk.shop.ui.order

import android.content.Context
import android.net.Uri
import com.honeymilk.shop.model.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.VerticalAlignment
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook

class OrderExportHelper {
    suspend fun exportOrdersToExcel(orders: List<Order>, uri: Uri, context: Context): Result<Unit> {
        return withContext(Dispatchers.IO) {
            val workbook: Workbook = XSSFWorkbook()
            try {
                val centeredCellStyle = workbook.createCellStyle().apply {
                    alignment = HorizontalAlignment.CENTER
                    verticalAlignment = VerticalAlignment.CENTER
                }
                val sheet = workbook.createSheet("Ordenes")
                val headerRow = sheet.createRow(0)
                val headers = listOf(
                    "NOMBRE",
                    "CORREO",
                    "CANTIDAD",
                    "DISEÑO",
                    "COLOR DE PRENDA",
                    "TIPO DE PRENDA",
                    "TALLA",
                    "PAGO",
                    "PAQUETERIA",
                    "ENVIO PAGADO",
                    "EXTRAS",
                    "NO. DE RASTREO"
                )

                for ((index, header) in headers.withIndex()) {
                    headerRow.createCell(index).apply {
                        setCellValue(header)
                        cellStyle = centeredCellStyle
                    }
                    sheet.setColumnWidth(index, calculateColumnWidth(header))
                }

                for ((rowIndex, order) in orders.withIndex()) {
                    val row: Row = sheet.createRow(rowIndex + 1)
                    val name = order.customer.name
                    val email = order.customer.email
                    val quantity = order.getTotalItemsQuantity().toString()
                    val designs = order.items.joinToString("\n") {
                        it.design.name + if (it.quantity > 1) " x ${it.quantity}" else ""
                    }
                    val colors = order.items.joinToString("\n") { it.color }
                    val types = order.items.joinToString("\n") { it.type }
                    val sizes = order.items.joinToString("\n") { it.size }
                    val subtotal = order.getSubtotal().toString()
                    val shippingCompany = order.shippingCompany
                    val isShippingPaid = if (order.isShippingPaid) "SI" else "NO"
                    val extras = order.extras
                    val trackingCode = order.trackingCode

                    val cells = listOf(
                        name,
                        email,
                        quantity,
                        designs,
                        colors,
                        types,
                        sizes,
                        subtotal,
                        shippingCompany,
                        isShippingPaid,
                        extras,
                        trackingCode
                    )

                    for ((index, cellValue) in cells.withIndex()) {
                        val cell = row.createCell(index)
                        cell.setCellValue(cellValue)
                        cell.cellStyle = centeredCellStyle
                        val currentWidth = sheet.getColumnWidth(index)
                        val newWidth = calculateColumnWidth(cellValue)
                        if (newWidth > currentWidth) {
                            sheet.setColumnWidth(index, newWidth)
                        }
                    }
                }

                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    workbook.write(outputStream)
                }

                Result.success(Unit)

            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure(e)
            } finally {
                try {
                    workbook.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun calculateColumnWidth(text: String): Int {
        return (text.length * 256) + 200
    }

}