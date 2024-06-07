package com.example.helpers

import com.example.config.MAX_ORDER_AMOUNT
import com.example.config.MAX_ORDER_DESCRIPTION_LENGTH
import com.example.config.MIN_ORDER_AMOUNT
import com.example.config.MIN_ORDER_DESCRIPTION_LENGTH
import java.util.*

class OrdersHelper {
    fun amountLimitsChecker (amount: Int): Boolean {
        return amount in MIN_ORDER_AMOUNT ..MAX_ORDER_AMOUNT
    }

    fun descriptionLengthChecker (description: String): Boolean {
        return description.length in MIN_ORDER_DESCRIPTION_LENGTH..MAX_ORDER_DESCRIPTION_LENGTH
    }

    fun orderUUIDChecker (id: String): Boolean {
        return id.length == 36 && "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}\$\n".toRegex().containsMatchIn(id)
    }
}