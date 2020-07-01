package pl.kapiz.minecraftmapy.ui.modules.newest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ModelAdapter
import dagger.android.support.DaggerFragment
import pl.kapiz.minecraftmapy.data.pojo.Map
import pl.kapiz.minecraftmapy.databinding.FragmentMapsBinding
import pl.kapiz.minecraftmapy.ui.base.MapItem
import pl.kapiz.minecraftmapy.ui.modules.main.MainActivity
import pl.kapiz.minecraftmapy.utils.observeNonNull
import pl.kapiz.minecraftmapy.utils.setEndlessScrollListener
import javax.inject.Inject

class NewestFragment : DaggerFragment() {

    companion object {

        fun newInstance() = NewestFragment()
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val newestViewModel: NewestViewModel by viewModels { viewModelFactory }

    private lateinit var b: FragmentMapsBinding

    private lateinit var mapsAdapter: ModelAdapter<Map, MapItem>
    private lateinit var adapter: FastAdapter<MapItem>

    private val activity by lazy { getActivity() as MainActivity }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        b = FragmentMapsBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
    }

    private fun initView() {
        mapsAdapter = ModelAdapter { MapItem(it) }

        newestViewModel.apply {
            init()

            maps.observe(viewLifecycleOwner, Observer { maps ->
                b.mapProgress.visibility = View.GONE
                b.mapList.visibility = View.VISIBLE
                mapsAdapter.setNewList(maps)
            })

            selectedMap.observeNonNull(viewLifecycleOwner, Observer { map ->
                val action = NewestFragmentDirections.actionNavigationNewestToMap(map)
                findNavController().navigate(action)
            })
        }

        adapter = FastAdapter.with(mapsAdapter).apply {
            onClickListener = newestViewModel::onItemClicked
        }

        b.mapList.apply {
            layoutManager = LinearLayoutManager(context)
            setEndlessScrollListener(20) {
                newestViewModel.downloadNextPage()
            }
            adapter = this@NewestFragment.adapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }
}
