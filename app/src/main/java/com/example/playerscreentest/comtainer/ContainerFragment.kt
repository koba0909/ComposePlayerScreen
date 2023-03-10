package com.example.playerscreentest.comtainer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.viewModels
import com.example.playerscreentest.BaseDataBindingFragment
import com.example.playerscreentest.ContainerScreen
import com.example.playerscreentest.R
import com.example.playerscreentest.databinding.FragmentContainerBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContainerFragment :
    BaseDataBindingFragment<FragmentContainerBinding>(R.layout.fragment_container) {

    private val containerViewModel: ContainerViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val root = super.onCreateView(inflater, container, savedInstanceState)

        binding.composeView.apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed,
            )
            setContent {
                MaterialTheme {
                    ContainerScreen(
                        containerViewModel = containerViewModel
                    )
                }
            }
        }

        return root
    }
}
