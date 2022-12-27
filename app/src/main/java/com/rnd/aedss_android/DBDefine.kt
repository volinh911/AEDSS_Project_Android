package com.rnd.aedss_android

public class DBDefine {
    companion object {
        val dayList = listOf<String>("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
        val periodList = listOf<String>("AM", "PM")

        fun createHourList() : List<String> {
            var hourList = mutableListOf<String>()
            for (i in 1 until 13) {
                if (i < 10) {
                    hourList.add("0$i")
                } else {
                    hourList.add("$i")
                }
            }
            return hourList
        }

        fun createMinList() : List<String> {
            var minList = mutableListOf<String>()
            for (i in 0 until 60) {
                if (i < 10) {
                    minList.add("0$i")
                } else {
                    minList.add("$i")
                }
            }
            return minList
        }

        //for device
        const val acDevice = 0
        const val lightDevice = 1
        const val doorDevice = 2

    }
}