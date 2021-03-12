package jp.techacademy.satoshi.tanaka.autoslideshowapp

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.provider.MediaStore
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.media.Image
import android.os.Handler
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*
import android.view.View
import com.google.android.material.snackbar.Snackbar
import java.time.temporal.ValueRange
import java.util.*
import android.util.Log.d as d1
import android.view.Menu
import android.view.MenuItem

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
                Log.d("Debug", "通過1")
            } else {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSONS_REQUEST_CODE
                )
                Log.d("Debug", "通過2")
            }
        } else {
            getContentsInfo()
            Log.d("Debug", "通過3")
        }
        Log.d("Debug", "mtimer(ビフォー1)=${mtimer}")
        Start_Stop.setOnClickListener {
            Log.d("Debug", "mtimer(ビフォー2)=${mtimer}")



            if (mtimer == null) {
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
                mtimer = null
                Log.d("Debug", "mtimer（アフター3）=${mtimer}")
            }

        }
    }

    private fun showAlertDialog() {
        Log.d("Debug", "アラートダイアログ通過")
        //アラートダイアログ.ビルダークラスを使用しアラートダイアログの準備をする
        val Warning_Message = AlertDialog.Builder(this)
        val alertDialog = Warning_Message.create()

        Warning_Message.setTitle("日本アニメ漫画図書館(JMCL)からのお願い")
        Warning_Message.setMessage(
            "このアプリはお客様のスマートフォン内に保存されている画像にアクセスし、スライドショー形式で表示するものです。\n" +
                    "そのためお客様におかれましては本アプリからストレージへのアクセスを許可していただきますようお願い申しあげます。\n \n" +
                    "JMACLアプリ制作局"
        )
        Warning_Message.setPositiveButton("やり直す") { _, _ ->
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSONS_REQUEST_CODE
            )
        }

        Warning_Message.setNegativeButton("拒否")
        { _, _ ->
            Log.d("Debug","拒否られました")
            /*Snackbar.make(View(applicationContext), "スマホのストレージにアクセスができないため本アプリを使用できません", Snackbar.LENGTH_LONG)
                .show()*/

        }
        Warning_Message.show()
    }
//private lateinit var Warning_Message:Context

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        Log.d("Debug", "通過4")
        when (requestCode) {
            PERMISSONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo()
                    Log.d("Debug", "通過5")
                } else {
                    Log.d("Debug", "通過6")
                    showAlertDialog()
                    Log.d("Debug", "通過7")
                }
        }
        /* } catch (e:Exception) {
             //alertDialog.show()
             Snackbar.make(
                 View(this),
                 "スマホのストレージにアクセスができないため本アプリを使用できません",
                 Snackbar.LENGTH_LONG
             ).show()
         }*/

    }


    private fun getContentsInfo() {
        Log.d("Debug", "通過7")
        val resolver = contentResolver
        Search = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,//データの種類
            null,//項目
            null,//フィルタ条件
            null,//フィルタ用パラ
            null//ソート
        )!!
        if (Search.moveToFirst()) {
            Log.d("Debug", "通過8")
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