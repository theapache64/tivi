/*
 * Copyright 2021 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.tivi.common.compose

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.commit
import androidx.fragment.app.findFragment
import androidx.viewbinding.ViewBinding

@Composable
fun <T : View> FragmentAwareAndroidView(
    factory: (Context) -> T,
    @IdRes fragmentContainerId: Int,
    modifier: Modifier = Modifier,
    update: (T) -> Unit = {}
) {
    val fragmentContainerViews = remember { mutableStateListOf<FragmentContainerView>() }
    AndroidView(factory = factory, modifier = modifier) { root ->
        fragmentContainerViews.clear()
        val rootGroup = root as? ViewGroup
        if (rootGroup != null) {
            findFragmentContainerViews(rootGroup, fragmentContainerViews)
        }
        update(root)
    }

    SyncFragments(
        fragmentContainerViews = fragmentContainerViews,
        fragmentContainerId = fragmentContainerId
    )
}

@Composable
fun <T : ViewBinding> FragmentAwareAndroidViewBinding(
    bindingBlock: (LayoutInflater, ViewGroup, Boolean) -> T,
    @IdRes fragmentContainerId: Int,
    modifier: Modifier = Modifier,
    update: T.() -> Unit = {}
) {
    val fragmentContainerViews = remember { mutableStateListOf<FragmentContainerView>() }
    AndroidViewBinding(bindingBlock, modifier = modifier) {
        fragmentContainerViews.clear()
        val rootGroup = root as? ViewGroup
        if (rootGroup != null) {
            findFragmentContainerViews(rootGroup, fragmentContainerViews)
        }
        update()
    }

    SyncFragments(
        fragmentContainerViews = fragmentContainerViews,
        fragmentContainerId = fragmentContainerId
    )
}

@Composable
private fun SyncFragments(
    fragmentContainerViews: List<FragmentContainerView>,
    @IdRes fragmentContainerId: Int,
) {
    val activity = LocalContext.current as FragmentActivity
    for (container in fragmentContainerViews) {
        DisposableEffect(container) {
            // Find the right FragmentManager
            val fragmentManager = try {
                val parentFragment = container.findFragment<Fragment>()
                parentFragment.childFragmentManager
            } catch (e: Exception) {
                activity.supportFragmentManager
            }
            // Now find the fragment inflated via the FragmentContainerView
            val existingFragment = fragmentManager.findFragmentById(fragmentContainerId)
            if (existingFragment != null) {
                val fragmentView = existingFragment.requireView()
                // Remove the Fragment from whatever old parent it had
                // (this is most likely an old binding if it is non-null)
                (fragmentView.parent as? ViewGroup)?.run { removeView(fragmentView) }
                // Re-add it to the layout if it was moved to RESUMED before
                // this Composable ran
                container.addView(existingFragment.requireView())
            }
            onDispose {
                if (existingFragment != null && !fragmentManager.isStateSaved) {
                    // If the state isn't saved, that means that some state change
                    // has removed this Composable from the hierarchy
                    fragmentManager.commit {
                        remove(existingFragment)
                    }
                }
            }
        }
    }
}

private fun findFragmentContainerViews(
    viewGroup: ViewGroup,
    list: SnapshotStateList<FragmentContainerView>
) {
    viewGroup.forEach {
        if (it is FragmentContainerView) {
            list += it
        } else if (it is ViewGroup) {
            findFragmentContainerViews(it, list)
        }
    }
}
