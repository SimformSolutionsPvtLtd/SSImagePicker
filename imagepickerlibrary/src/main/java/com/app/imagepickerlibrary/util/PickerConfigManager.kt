package com.app.imagepickerlibrary.util

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryOwner
import com.app.imagepickerlibrary.EXTRA_IMAGE_PICKER_CONFIG
import com.app.imagepickerlibrary.getModel
import com.app.imagepickerlibrary.model.PickerConfig

internal class PickerConfigManager(registryOwner: SavedStateRegistryOwner) :
    SavedStateRegistry.SavedStateProvider {
    companion object {
        const val PICKER_CONFIG_MANAGER = "picker_config_manage"
    }

    private var pickerConfig = PickerConfig.defaultPicker()

    init {
        registryOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_CREATE) {
                val registry = registryOwner.savedStateRegistry
                registry.registerSavedStateProvider(PICKER_CONFIG_MANAGER, this)
                val previousState = registry.consumeRestoredStateForKey(PICKER_CONFIG_MANAGER)
                if (previousState != null && previousState.containsKey(EXTRA_IMAGE_PICKER_CONFIG)) {
                    pickerConfig = previousState.getModel() ?: PickerConfig.defaultPicker()
                }
            }
        })
    }

    override fun saveState(): Bundle {
        return bundleOf(EXTRA_IMAGE_PICKER_CONFIG to pickerConfig)
    }

    fun getPickerConfig(): PickerConfig {
        return pickerConfig
    }
}
