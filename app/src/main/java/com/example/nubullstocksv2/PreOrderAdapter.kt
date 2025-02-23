package com.example.nubullstocksv2

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class PreOrderAdapter(private val preOrderList: MutableList<PreOrder>) :
    RecyclerView.Adapter<PreOrderAdapter.PreOrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreOrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pre_order, parent, false)
        return PreOrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: PreOrderViewHolder, position: Int) {
        val preOrder = preOrderList[position]
        holder.tvCustomerName.text = "Customer: ${preOrder.firstName} ${preOrder.lastName}"
        holder.tvProductName.text = "Product: ${preOrder.productName}"
        holder.tvQuantity.text = "Stock: ${preOrder.stock}"
        holder.tvStatus.text = "Status: ${preOrder.status}"

        holder.btnToggleStatus.setOnClickListener {
            if (preOrder.status == "Pending") {
                showDateTimePicker(holder, preOrder, position) // Show date/time picker for "Pending" status
            } else {
                updateStatus(holder, preOrder, "Pending", "", position)
            }
        }
    }

    private fun showDateTimePicker(holder: PreOrderViewHolder, preOrder: PreOrder, position: Int) {
        val context = holder.itemView.context
        val calendar = Calendar.getInstance()

        // Date Picker Dialog
        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val timePickerDialogStart = TimePickerDialog(
                    context,
                    { _, hourOfDayStart, minuteStart ->
                        val timePickerDialogEnd = TimePickerDialog(
                            context,
                            { _, hourOfDayEnd, minuteEnd ->
                                val selectedDate = "$dayOfMonth/${month + 1}/$year"
                                val startTime = formatTime(hourOfDayStart, minuteStart)
                                val endTime = formatTime(hourOfDayEnd, minuteEnd)

                                val pickupDateTime = "$selectedDate $startTime - $endTime"
                                updateStatus(holder, preOrder, "Confirmed", pickupDateTime, position)
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            false
                        )
                        timePickerDialogEnd.show()
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    false
                )
                timePickerDialogStart.show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun formatTime(hourOfDay: Int, minute: Int): String {
        val amPm = if (hourOfDay < 12) "AM" else "PM"
        var formattedHour = if (hourOfDay > 12) hourOfDay - 12 else hourOfDay
        if (formattedHour == 0) formattedHour = 12
        val formattedMinute = if (minute < 10) "0$minute" else "$minute"
        return "$formattedHour:$formattedMinute $amPm"
    }

    private fun updateStatus(holder: PreOrderViewHolder, preOrder: PreOrder, newStatus: String, pickupDateTime: String, position: Int) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("pre_orders").child(preOrder.orderId)

        val updates = mapOf(
            "status" to newStatus,
            "pickupDateTime" to pickupDateTime
        )

        databaseRef.updateChildren(updates).addOnSuccessListener {
            preOrderList[position] = preOrder.copy(status = newStatus, pickupDateTime = pickupDateTime)
            notifyItemChanged(position)
            holder.tvStatus.text = "Status: $newStatus\nPickup: $pickupDateTime"
            sendEmail(holder.itemView.context, preOrder, pickupDateTime)
            Toast.makeText(holder.itemView.context, "Status updated to $newStatus", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(holder.itemView.context, "Failed to update status", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendEmail(context: Context, preOrder: PreOrder, pickupDateTime: String) {
        val subject = "Your Pre-Order Confirmation - NUBULLSTOCKS"
        val message = """
            Hello ${preOrder.firstName} ${preOrder.lastName},

            Your pre-order for ${preOrder.productName} has been confirmed.

            Pickup Schedule:
            Date & Time: $pickupDateTime

            Please bring your student ID when picking up your order.

            Thank you for shopping with us!

            - NUBULLSTOCKS Team
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(preOrder.userEmail))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, message)
        }

        try {
            context.startActivity(Intent.createChooser(intent, "Send Email..."))
        } catch (e: Exception) {
            Log.e("PreOrderAdapter", "No email clients installed: ${e.message}")
            Toast.makeText(context, "Email client not found", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int = preOrderList.size

    inner class PreOrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCustomerName: TextView = itemView.findViewById(R.id.tvCustomerName)
        val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        val tvQuantity: TextView = itemView.findViewById(R.id.tvQuantity)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val btnToggleStatus: Button = itemView.findViewById(R.id.btnToggleStatus)
    }
}
