package com.example.maxs.weather_station

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import com.google.android.things.contrib.driver.apa102.Apa102
import com.google.android.things.contrib.driver.button.ButtonInputDriver
import com.google.android.things.contrib.driver.ht16k33.AlphanumericDisplay
import com.google.android.things.contrib.driver.rainbowhat.RainbowHat
import java.io.IOException

/**
 * Skeleton of an Android Things activity.
 *
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 *
 * <pre>{@code
 * val service = PeripheralManagerService()
 * val mLedGpio = service.openGpio("BCM6")
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
 * mLedGpio.value = true
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 *
 */
class MainActivity : Activity() {

    private val TAG : String = this.javaClass.simpleName
    private val LEDSTRIP_BRIGHTNESS = 1

    private lateinit var mButtonDriver : ButtonInputDriver
    private lateinit var mDisplay : AlphanumericDisplay
    private lateinit var mLedstrip : Apa102

    private var mDisplayString : String = "Dima"

    var displayStatus : Boolean = false
    var ledStripStatus : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initButton()
        Log.d(TAG, "Weather Station Started")
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            Toast.makeText(this, "KeyEvent happend", Toast.LENGTH_SHORT).show()
            return true
        }
        return super.onKeyUp(keyCode, event)
    }

    private fun initButton() {
        mButtonDriver = RainbowHat.createButtonCInputDriver(KeyEvent.KEYCODE_ENTER)
        mButtonDriver.register()
    }


    fun initDisplay (view: View) {
        if (!displayStatus) {
            try {
                mDisplay = RainbowHat.openDisplay()
                mDisplay.setEnabled(true)
                mDisplay.display(mDisplayString)
                Log.d(TAG, "Initialized I2C Display")

                displayStatus = true
            } catch (e: IOException) {
                throw RuntimeException("Error initializing display $e")
            }
        }
        else
            Toast.makeText(this, "Already turned on", Toast.LENGTH_SHORT).show()
    }

    fun initLedstrip (view: View) {
        if (!ledStripStatus) {
            try {
                mLedstrip = RainbowHat.openLedStrip()
                mLedstrip.brightness = LEDSTRIP_BRIGHTNESS
                val colors = IntArray(7)
                colors[0] = Color.RED
                colors[1] = Color.YELLOW
                colors[2] = Color.BLUE
                colors[3] = Color.CYAN
                colors[4] = Color.WHITE
                colors[5] = Color.GRAY
                colors[6] = Color.GREEN

                // Because of a known APA102 issue, write the initial value twice.
                mLedstrip.write(colors)
                mLedstrip.write(colors)

                ledStripStatus = true

                Log.d(TAG, "Initialized SPI LED strip")

            } catch (e: IOException) {
                throw RuntimeException("Error initializing LED strip", e)
            }
        }
        else
            Toast.makeText(this, "Already turned on", Toast.LENGTH_SHORT).show()
    }

    fun destoyDisplay(view: View) {
        if(displayStatus) {
            try {
                mDisplay.clear()
                mDisplay.setEnabled(false)
                mDisplay.close()

                displayStatus = false
            }
            catch (e: IOException) {
                Log.e(TAG, "Error closing display $e")
            }
        }
        else
            Toast.makeText(this, "Already turned off", Toast.LENGTH_SHORT).show()
    }

    fun destroyLedstrip(view: View) {
        if (ledStripStatus) {
            try {
                mLedstrip.brightness = 0
                mLedstrip.write(IntArray(7))
                mLedstrip.close()

                ledStripStatus = false
            } catch (e: IOException) {
                Log.e(TAG, "Error closing LED strip $e")
            }
        }
        else
            Toast.makeText(this, "Already turned off", Toast.LENGTH_SHORT).show()
    }
}