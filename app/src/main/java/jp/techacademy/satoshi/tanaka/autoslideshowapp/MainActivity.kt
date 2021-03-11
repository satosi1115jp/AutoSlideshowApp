package jp.techacademy.satoshi.tanaka.autoslideshowapp

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.provider.MediaStore
import android.content.ContentUris
import android.database.Cursor
import android.media.Image
import android.os.Handler
import kotlinx.android.synthetic.main.activity_main.*
import java.time.temporal.ValueRange
import java.util.*
import android.util.Log.d as d1

class MainActivity : AppCompatActivity() {

    private val PERMISSONS_REQUEST_CODE = 50

    private var mtimer: Timer? = null
    private var mtimerSec = 0.0
    private var mHandler = Handler()


    //画像の情報を取得する

    private lateinit var Search: Cursor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                getContentsInfo()
            } else {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSONS_REQUEST_CODE
                )
            }
        } else {
            getContentsInfo()
        }
        Log.d("Debug", "mtimer(ビフォー1)=${mtimer}")
        Start_Stop.setOnClickListener {
            Log.d("Debug", "mtimer(ビフォー2)=${mtimer}")



            if (mtimer==null) {//ToDo　動いているかの判定を行う
                mtimer = Timer()
                Start_Stop.text = "停止"
                Log.d("Debug", "mtimer(ビフォー)=${mtimer}")
                mtimer!!.schedule(object : TimerTask() {
                    override fun run() {
                        mtimerSec += 20
                        mHandler.post {

                            if (Search.isLast()) {
                                if (Search.moveToFirst()) {
                                    val fieldIndex =
                                        Search.getColumnIndex(MediaStore.Images.Media._ID)
                                    val id = Search.getLong(fieldIndex)
                                    val ImageURI =
                                        ContentUris.withAppendedId(
                                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                            id
                                        )
                                    picture_View.setImageURI(ImageURI)
                                }
                            } else if (Search.moveToNext()) {
                                    val fieldIndex =
                                        Search.getColumnIndex(MediaStore.Images.Media._ID)
                                    val id = Search.getLong(fieldIndex)
                                    val ImageURI =
                                        ContentUris.withAppendedId(
                                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                            id
                                        )
                                    picture_View.setImageURI(ImageURI)
                                    }
                        }
                    }
                }, 2000, 2000)
                 Log.d("Debug", "mtimer（アフター）=${mtimer}")
            } else {
                Log.d("Debug", "mtimer（アフター2）=${mtimer}")
                mtimer!!.cancel()
                Start_Stop.text = "再生"
                mtimer=null
                Log.d("Debug", "mtimer（アフター3）=${mtimer}")
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo()
                }
        }
    }

    private fun getContentsInfo() {
        val resolver = contentResolver
        Search= resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,//データの種類
            null,//項目
            null,//フィルタ条件
            null,//フィルタ用パラ
            null//ソート
        )!!
        if (Search.moveToFirst()) {
            //indexからIDを取得、そのIDからがぞうのURIをゲット
            val fieldIndex = Search.getColumnIndex(MediaStore.Images.Media._ID)
            val id = Search.getLong(fieldIndex)
            val ImageURI =
                ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

            picture_View.setImageURI(ImageURI)

        }



        Next.setOnClickListener {


            if (Search.isLast()) {
                if (Search.moveToFirst()) {
                    val fieldIndex = Search.getColumnIndex(MediaStore.Images.Media._ID)
                    val id = Search.getLong(fieldIndex)
                    val ImageURI =
                        ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                    picture_View.setImageURI(ImageURI)
                }
            } else if (Search.moveToNext()) {
                val fieldIndex = Search.getColumnIndex(MediaStore.Images.Media._ID)
                val id = Search.getLong(fieldIndex)
                val ImageURI =
                    ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                picture_View.setImageURI(ImageURI)

            }
        }
        Back.setOnClickListener {


            if (Search.isFirst()) {
                if (Search.moveToLast()) {
                    val fieldIndex = Search.getColumnIndex(MediaStore.Images.Media._ID)
                    val id = Search.getLong(fieldIndex)
                    val ImageURI =
                        ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                    picture_View.setImageURI(ImageURI)
                }
            } else
                if (Search.moveToPrevious()) {
                    val fieldIndex = Search.getColumnIndex(MediaStore.Images.Media._ID)
                    val id = Search.getLong(fieldIndex)
                    val ImageURI =
                        ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                    picture_View.setImageURI(ImageURI)

                }
        }
    }


}


