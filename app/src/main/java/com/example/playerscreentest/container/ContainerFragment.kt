package com.example.playerscreentest.container

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.ConstraintSet.Constraint
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.example.playerscreentest.BaseDataBindingFragment
import com.example.playerscreentest.R
import com.example.playerscreentest.databinding.FragmentContainerBinding
import com.example.playerscreentest.databinding.FragmentContainerPortraitBinding
import com.example.playerscreentest.listpopup.dpToPx
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

enum class LayoutState {
    PORTRAIT, LANDSCAPE, LANDSCAPE_BOTTOM, LANDSCAPE_BOTTOM_6
}

@AndroidEntryPoint
class ContainerFragment :
    BaseDataBindingFragment<FragmentContainerPortraitBinding>(R.layout.fragment_container_portrait) {

    private val containerViewModel: ContainerViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val root = super.onCreateView(inflater, container, savedInstanceState)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collectFlows()

        binding.fcvTop.setOnClickListener {
            when (containerViewModel.getLayoutState()) {
                LayoutState.PORTRAIT -> {
                    requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    containerViewModel.setLayoutState(LayoutState.LANDSCAPE)
                }
                LayoutState.LANDSCAPE -> {
                    changeToLandscapeBottomConstraintSet()
                    containerViewModel.setLayoutState(LayoutState.LANDSCAPE_BOTTOM)
                }
                LayoutState.LANDSCAPE_BOTTOM -> {
                    changeToLandscapeBottom6ConstraintSet()
                    containerViewModel.setLayoutState(LayoutState.LANDSCAPE_BOTTOM_6)
                }
                LayoutState.LANDSCAPE_BOTTOM_6 -> {
                    requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    containerViewModel.setLayoutState(LayoutState.PORTRAIT)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("hugh", "onResume sate: ${containerViewModel.getLayoutState()}")
    }

    private fun changeToLandscapeBottom6ConstraintSet() {
        changeToLandscapeBottomConstraintSet()
        binding.fcvBottom.updateLayoutParams {
            height = dpToPx(requireContext(), 150f)
        }
    }

    private val isPortrait: Boolean
        get() = resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    private fun collectFlows() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                containerViewModel.run {
                    effect.collect {
                        if (isPortrait) {
                            requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                        } else {
                            requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                        }
                    }
                }
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        Log.d("hugh", "configuration changed sate: ${containerViewModel.getLayoutState()}")

        if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            changeToPortraitConstraintSet()
        } else {
            changeToLandscapeConstraintSet()
        }
    }

    private val portraitConstraintSet by lazy {
        ConstraintSet().apply {
            clone(context, R.layout.fragment_container_portrait)
        }
    }

    private val landscapeConstraintSet by lazy {
        ConstraintSet().apply {
            clone(context, R.layout.fragment_container_landscape)
        }
    }

    private val landscapeConstraintBottomSet by lazy {
        ConstraintSet().apply {
            clone(context, R.layout.fragment_container_landscape_bottom)
        }
    }

    private fun changeToLandscapeConstraintSet() {
        landscapeConstraintSet.applyTo(binding.clRoot)
    }

    private fun changeToPortraitConstraintSet() {
        portraitConstraintSet.applyTo(binding.clRoot)
    }


    private fun changeToLandscapeBottomConstraintSet() {
//        TransitionManager.beginDelayedTransition(binding.clRoot, ChangeBounds().apply { duration = 300 })
        landscapeConstraintBottomSet.applyTo(binding.clRoot)
    }
}
