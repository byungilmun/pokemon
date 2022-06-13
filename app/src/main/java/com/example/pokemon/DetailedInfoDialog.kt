package com.example.pokemon

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.pokemon.domain.PokemonDetailedInfo

class DetailedInfoDialog(context: Context, private val mListener: View.OnClickListener) :
    Dialog(context) {
    private val PLACE_HOLDER_COLOR_DRAWABLE: ColorDrawable
    lateinit var mImageView: ImageView
    lateinit var mNameText: TextView
    lateinit var mHeightText: TextView
    lateinit var mWeightText: TextView
    lateinit var mLocationText: TextView
    lateinit var mMapIcon: ImageView
    lateinit var mDetailedInfo: PokemonDetailedInfo


    companion object {
        val KEY_LATITUDE_LONGITUDE = "key_latitude_longitude"
    }

    init {
        PLACE_HOLDER_COLOR_DRAWABLE =
            ColorDrawable(getContext().resources.getColor(R.color.place_holder))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.details_layout)
        val wl = WindowManager.LayoutParams()
        wl.windowAnimations = R.style.AnimationPopupStyle
        wl.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        window?.attributes = wl
        window?.setDimAmount(0.5f)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setCanceledOnTouchOutside(false)
        initializeLayout()
    }

    private fun initializeLayout() {
        mImageView = findViewById(R.id.imageView) as ImageView
        mNameText = findViewById(R.id.details_name) as TextView
        mHeightText = findViewById(R.id.details_height) as TextView
        mWeightText = findViewById(R.id.details_weight) as TextView
        mLocationText = findViewById(R.id.details_habitat) as TextView
        val btnOk =
            findViewById(R.id.btn_ok) as Button
        btnOk.setOnClickListener(mListener)
        mMapIcon = findViewById(R.id.map_icon) as ImageView
        mMapIcon?.setOnClickListener {
            val intent = Intent(context, MapsActivity::class.java)
            intent.putExtra(
                KEY_LATITUDE_LONGITUDE, mDetailedInfo?.let {
                    doubleArrayOf(it.latitude.toDouble(), it.longitude.toDouble())
                }
            )
            context.startActivity(intent)
        }
    }

    fun updateDetailedInfo(detailedInfo: PokemonDetailedInfo?) {
        if (detailedInfo == null) return
        if (detailedInfo.getSprite() != null) {
            mImageView?.let {
                Glide.with(context).load(detailedInfo.getSprite())
                    .placeholder(PLACE_HOLDER_COLOR_DRAWABLE).into(it)
            }
        }
        val names: Array<String> = detailedInfo.getNames()
        if (!names.isNullOrEmpty()) {
            val name = StringBuilder().append(names[0])
            if (names.size > 1) {
                for (i in 1 until names.size) {
                    name.append("\n(").append(names[i]).append(",")
                }
                name.deleteCharAt(name.length - 1)
                name.append(")")
            }
            mNameText!!.text = name.toString()
        }
        mHeightText!!.text = java.lang.String.format(
            context.resources.getString(R.string.height_unit),
            detailedInfo.getHeight()
        )
        mWeightText!!.text = java.lang.String.format(
            context.resources.getString(R.string.weight_unit),
            detailedInfo.getWeight()
        )
        if (detailedInfo.isHabitatKnown()) {
            val address = getAddress(detailedInfo)
            mLocationText!!.text = address
            if (mMapIcon!!.visibility != View.VISIBLE) {
                mMapIcon!!.visibility = View.VISIBLE
            }
        } else {
            mLocationText!!.text = context.resources.getString(R.string.habitat_unknown)
            if (mMapIcon!!.visibility != View.GONE) {
                mMapIcon!!.visibility = View.GONE
            }
        }
        mDetailedInfo = detailedInfo
    }

    private fun getAddress(detailedInfo: PokemonDetailedInfo?): String {
        var localAddress = ""
        val geocoder = Geocoder(context)
        if (detailedInfo != null) {
            try {
                val address =
                    geocoder.getFromLocation(
                        detailedInfo.getLatitude().toDouble(),
                        detailedInfo.getLongitude().toDouble(),
                        1
                    )
                if (address != null && address.size > 0) {
                    val admin =
                        if (address[0].adminArea != null) address[0]
                            .adminArea else ""
                    val locality =
                        if (address[0].locality != null) address[0]
                            .locality else ""
                    val thoroughfare =
                        if (address[0].thoroughfare != null) address[0]
                            .thoroughfare else ""
                    localAddress = "$admin $locality $thoroughfare"
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return localAddress.trim { it <= ' ' }
    }
}