package com.television.myprojecttv

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.television.myprojecttv.networkcall.RetrofitService
import java.util.*

class MainFragment: BrowseSupportFragment(), OnItemViewClickedListener {
    lateinit var viewModel: MainViewModel


    private val mHandler = Handler()
    private lateinit var mBackgroundManager: BackgroundManager
    private var mDefaultBackground: Drawable? = null
    private lateinit var mMetrics: DisplayMetrics
    private var mBackgroundTimer: Timer? = null
    private var mBackgroundUri: String? = null


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val retrofitService = RetrofitService.getInstance()
        val mainRepository = MainRepository(retrofitService)
        viewModel = ViewModelProvider(this, MyViewModelFactory(mainRepository)).get(MainViewModel::class.java)
        viewModel.getAllMovies()
        prepareBackgroundManager()

        viewModel.loading.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if(it){
                Log.d(TAG, "it:>> Failed : ${it}")
            }else{
                Log.d(TAG, "it:>> Success : ${it}")
                setUI()

            }
        })

        viewModel.movieList.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if(it.isNotEmpty()){

//                MovieList.list.toMutableList().removeAll(MovieList.list)

                loadRows(it)
            }
        })

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: " + mBackgroundTimer?.toString())
        mBackgroundTimer?.cancel()
    }

    private fun prepareBackgroundManager() {

        mBackgroundManager = BackgroundManager.getInstance(requireActivity())
        mBackgroundManager.attach(requireActivity().window)
        mDefaultBackground = ContextCompat.getDrawable(requireActivity(), R.drawable.default_background)
        mMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(mMetrics)
    }

    private fun setUI() {
        title = "BlazingTV"
        headersState = HEADERS_ENABLED
        brandColor =  ContextCompat.getColor(requireActivity(), R.color.teal_bg)
        searchAffordanceColor = ContextCompat.getColor(requireActivity(), R.color.teal_700)

        isHeadersTransitionOnBackEnabled = true

        setOnSearchClickedListener {
            Toast.makeText(requireActivity(), "Implement your own in-app search", Toast.LENGTH_LONG)
                .show()
        }

//        setBadgeDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_launcher_foreground))
//        loadRows()

        setOnItemViewClickedListener(this)
        onItemViewSelectedListener = ItemViewSelectedListener()
    }

    private inner class ItemViewSelectedListener: OnItemViewSelectedListener{
        override fun onItemSelected(
            itemViewHolder: Presenter.ViewHolder?, item: Any?,
            rowViewHolder: RowPresenter.ViewHolder, row: Row
        ) {
            if (item is Movie) {
                mBackgroundUri = item.backgroundImageUrl
                startBackgroundTimer()
            }
        }

    }

    private fun startBackgroundTimer() {
        mBackgroundTimer?.cancel()
        mBackgroundTimer = Timer()
        mBackgroundTimer?.schedule(UpdateBackgroundTask(), BACKGROUND_UPDATE_DELAY.toLong())
    }

    private inner class UpdateBackgroundTask : TimerTask() {

        override fun run() {
            mHandler.post { updateBackground(mBackgroundUri) }
        }
    }


    private fun updateBackground(uri: String?) {
        val width = mMetrics.widthPixels
        val height = mMetrics.heightPixels
        Glide.with(requireActivity())
            .load(uri)
            .centerCrop()
            .error(mDefaultBackground)
            .into<SimpleTarget<Drawable>>(
                object : SimpleTarget<Drawable>(width, height) {
                    override fun onResourceReady(
                        drawable: Drawable,
                        transition: Transition<in Drawable>?
                    ) {
                        mBackgroundManager.drawable = drawable
                    }
                })
        mBackgroundTimer?.cancel()
    }

    private fun loadRows(list: List<Movie>) {
//        val list = MovieList.list

        val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        val cardPresenter = CardPresenter()

        for (i in 0 until list.size) {
            if (i != 0) {
                Collections.shuffle(list)
            }
            val listRowAdapter = ArrayObjectAdapter(cardPresenter)
            for (j in 0 until list.size) {
//                listRowAdapter.add(list[j % 5])
                listRowAdapter.add(list[j])
            }
            val header = HeaderItem(i.toLong(), list[i].category)
            rowsAdapter.add(ListRow(header, listRowAdapter))
        }


        val gridHeader = HeaderItem(NUM_ROWS.toLong(), "PREFERENCES")

        val mGridPresenter = GridItemPresenter()
        val gridRowAdapter = ArrayObjectAdapter(mGridPresenter)
        gridRowAdapter.add(resources.getString(R.string.grid_view))
        gridRowAdapter.add(getString(R.string.error_fragment))
        gridRowAdapter.add(resources.getString(R.string.personal_settings))
        rowsAdapter.add(ListRow(gridHeader, gridRowAdapter))

        adapter = rowsAdapter
    }/*private fun loadRows() {
        *//*      var category1:HeaderItem  = HeaderItem(0, "Movies")
              var category2:HeaderItem  = HeaderItem(1, "Songs")


              var arrayObjectAdapter:ArrayObjectAdapter = ArrayObjectAdapter(MyPresenter())
              arrayObjectAdapter.add(SingleRowView("Sea", resources.getDrawable(androidx.vectordrawable.R.drawable.notification_template_icon_low_bg)))
              arrayObjectAdapter.add(ContextCompat.getDrawable(requireContext(), R.drawable.tile_img)?.let {
                  SingleRowView(
                      "Sea1",
                      it

                  )
              })
              arrayObjectAdapter.add(SingleRowView("Sea2", resources.getDrawable(androidx.vectordrawable.R.drawable.notification_action_background)))
              arrayObjectAdapter.add(SingleRowView("Sea3", resources.getDrawable(androidx.vectordrawable.R.drawable.notification_action_background)))


        var windowObjectAdapter:ArrayObjectAdapter = ArrayObjectAdapter(ListRowPresenter())

         windowObjectAdapter.add(ListRow(category1, arrayObjectAdapter))
        windowObjectAdapter.add(ListRow(category2, arrayObjectAdapter))
        adapter= windowObjectAdapter*//*

        val list = MovieList.list

        val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        val cardPresenter = CardPresenter()

        for (i in 0 until list.size) {
            if (i != 0) {
                Collections.shuffle(list)
            }
            val listRowAdapter = ArrayObjectAdapter(cardPresenter)
            for (j in 0 until list.size) {
//                listRowAdapter.add(list[j % 5])
                listRowAdapter.add(list[j])
            }
            val header = HeaderItem(i.toLong(), MovieList.MOVIE_CATEGORY[i])
            rowsAdapter.add(ListRow(header, listRowAdapter))
        }

        *//*for (i in 0 until NUM_ROWS) {
            if (i != 0) {
                Collections.shuffle(list)
            }
            val listRowAdapter = ArrayObjectAdapter(cardPresenter)
            for (j in 0 until NUM_COLS) {
                listRowAdapter.add(list[j % 5])
//                listRowAdapter.add(list[j])
            }
            val header = HeaderItem(i.toLong(), MovieList.MOVIE_CATEGORY[i])
            rowsAdapter.add(ListRow(header, listRowAdapter))
        }*//*

        val gridHeader = HeaderItem(NUM_ROWS.toLong(), "PREFERENCES")

        val mGridPresenter = GridItemPresenter()
        val gridRowAdapter = ArrayObjectAdapter(mGridPresenter)
        gridRowAdapter.add(resources.getString(R.string.grid_view))
        gridRowAdapter.add(getString(R.string.error_fragment))
        gridRowAdapter.add(resources.getString(R.string.personal_settings))
        rowsAdapter.add(ListRow(gridHeader, gridRowAdapter))

        adapter = rowsAdapter
    }*/

    override fun onItemClicked(
        itemViewHolder: Presenter.ViewHolder,
        item: Any?,
        rowViewHolder: RowPresenter.ViewHolder,
        row: Row?,
    ) {
        if(item is Movie){
            val intent:Intent = Intent(requireActivity(),DetailsActivity::class.java)
            intent.putExtra(DetailsActivity.MOVIE, item)

            val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                requireActivity(),
                (itemViewHolder.view as ImageCardView).mainImageView,
                DetailsActivity.SHARED_ELEMENT_NAME)
                .toBundle()

            startActivity(intent, bundle)
        } else if(item is String){
            if (item.contains(getString(R.string.error_fragment))) {
                val intent = Intent(requireActivity(), BrowseErrorActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(requireActivity(), item, Toast.LENGTH_SHORT).show()
            }
        }

    }

    private inner class GridItemPresenter : Presenter() {
        override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
            val view = TextView(parent.context)
            view.layoutParams = ViewGroup.LayoutParams(GRID_ITEM_WIDTH, GRID_ITEM_HEIGHT)
            view.isFocusable = true
            view.isFocusableInTouchMode = true
            view.setBackgroundColor(ContextCompat.getColor(requireActivity(), android.R.color.background_dark))
            view.setTextColor(Color.WHITE)
            view.gravity = Gravity.CENTER
            return ViewHolder(view)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
            (viewHolder.view as TextView).text = item as String
        }

        override fun onUnbindViewHolder(viewHolder: ViewHolder) {}
    }
    companion object {
        private val TAG = "MainFragment"

        private val BACKGROUND_UPDATE_DELAY = 300
        private val GRID_ITEM_WIDTH = 200
        private val GRID_ITEM_HEIGHT = 200
        private val NUM_ROWS = 6
        private val NUM_COLS = 15
    }

}

 /*private class MyPresenter: Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
        var icv:ImageCardView = ImageCardView(parent?.context)
        icv.cardType = BaseCardView.CARD_TYPE_INFO_UNDER_WITH_EXTRA
        icv.infoVisibility = BaseCardView.CARD_REGION_VISIBLE_ACTIVATED
        return ViewHolder(icv)
    }


     override fun onBindViewHolder(viewHolder: ViewHolder?, item: Any?) {
        var srv: SingleRowView? = item as SingleRowView
        var icv: ImageCardView = viewHolder?.view as ImageCardView
        icv.mainImage = srv?.image
        icv.setMainImageDimensions(313, 176)
        icv.titleText = srv?.name
        icv.contentText = "Movie decription...."
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {
        TODO("Not yet implemented")
    }


}
*/