package net.harupiza.macm.client

import com.fazecast.jSerialComm.SerialPort
import com.fazecast.jSerialComm.SerialPortDataListener
import com.fazecast.jSerialComm.SerialPortEvent
import net.fabricmc.api.ClientModInitializer
import net.harupiza.macm.DataStore
import net.harupiza.macm.Macm


class MacmClient : ClientModInitializer {

    override fun onInitializeClient() {
        try {
            val port = SerialPort.getCommPort("COM6")
            port.setBaudRate(9600)
            val opened = port.openPort()

            val buffer = StringBuilder()

            if (!opened) {
                Macm.LOGGER.error("Failed to open port")
            } else {
                Macm.LOGGER.info("Opened port")
            }

            port.addDataListener(object : SerialPortDataListener {
                override fun getListeningEvents(): Int {
                    return SerialPort.LISTENING_EVENT_DATA_AVAILABLE
                }

                override fun serialEvent(event: SerialPortEvent) {
                    try {
                        if (event.eventType != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) return
                        val newData = ByteArray(port.bytesAvailable())
                        val numRead = port.readBytes(newData, newData.size)
                        buffer.append(String(newData, 0, numRead))

                        var lineEndIndex: Int
                        while (buffer.indexOf("\n").also { lineEndIndex = it } != -1) {
                            val line = buffer.substring(0, lineEndIndex).trim()
                            buffer.delete(0, lineEndIndex + 1)
                            try {
                                DataStore.controlerval = line.toInt()
                            } catch (e: NumberFormatException) {
                                Macm.LOGGER.error(e.message)
                            }
                        }
                    } catch (e: Exception) {
                        Macm.LOGGER.error(e.message)
                    }
                }
            })
        } catch (e: Exception) {
            Macm.LOGGER.error(e.message)
        }
    }
}
