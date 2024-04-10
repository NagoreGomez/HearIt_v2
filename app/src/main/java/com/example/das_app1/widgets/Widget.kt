package com.example.das_app1.widgets

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.example.das_app1.model.entities.CompactPlaylist
import com.example.das_app1.widgets.WidgetReceiver.Companion.currentUserKey
import com.example.das_app1.widgets.WidgetReceiver.Companion.todayVisitsDataKey
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import com.example.das_app1.R
import com.example.das_app1.widgets.WidgetReceiver.Companion.UPDATE_ACTION


class Widget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {

        provideContent {
            Content()
        }
    }

    @Composable
    private fun Content() {

        val prefs = currentState<Preferences>()


        val user = prefs[currentUserKey]
        val data: String? = prefs[todayVisitsDataKey]
        val playlistList: List<CompactPlaylist> = if (data != null) Json.decodeFromString(data) else emptyList()


        Column(
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
            modifier = GlanceModifier
                .fillMaxSize()
                .background(color = Color.White)
                .padding(16.dp)
        ) {

            /*------------------------------------------------
            |                     Header                     |
            ------------------------------------------------*/

            Text(
                text = if (user != null) "Your playlist $user" else "All playlists",
                modifier = GlanceModifier.fillMaxWidth().padding(bottom = 16.dp),
                maxLines = 1
            )


            /*------------------------------------------------
            |               Body (Visit List)                |
            ------------------------------------------------*/

            when {

                //-----------   No user logged in   ------------//
                user == null -> {
                    Column(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = GlanceModifier.fillMaxSize().defaultWeight()
                    ) {
                        Text(text = "Login please")
                    }
                }

                //------   User logged in but no visits   ------//
                playlistList.isEmpty() -> {
                    Column(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = GlanceModifier.fillMaxSize().defaultWeight()
                    ) {
                        Text(text = "No playlists")
                    }
                }

                //-------   User logged in with visits   -------//
                else -> {
                    LazyColumn(modifier = GlanceModifier.fillMaxSize().defaultWeight()) {
                        items(playlistList, itemId = { it.hashCode().toLong() }) { item ->
                            VisitItem(playlists = item)
                        }
                    }
                }
            }


            /*------------------------------------------------
            |                 Refresh Button                 |
            ------------------------------------------------*/

            Spacer(GlanceModifier.height(8.dp))

            Image(
                provider = ImageProvider(R.drawable.playlist),
                contentDescription = "Refresh",
                modifier = GlanceModifier.size(24.dp).clickable{ actionRunCallback<RefreshAction>()}
            )
        }
    }


     class RefreshAction : ActionCallback {

        override suspend fun onAction(
            context: Context,
            glanceId: GlanceId,
            parameters: ActionParameters
        ) {
            Widget().update(context, glanceId)

            /*
            val intent = Intent(context, WidgetReceiver::class.java).apply { action = UPDATE_ACTION }
            context.sendBroadcast(intent)
             */

        }
    }


    /*************************************************
     **            Widget Visit List Item           **
     *************************************************/

    @Composable
    private fun VisitItem(playlists: CompactPlaylist) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = GlanceModifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
        ) {

            /*------------------------------------------------
            |                   Visit Data                   |
            ------------------------------------------------*/

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = GlanceModifier.fillMaxWidth().defaultWeight()
            ) {

                //------------------   Hour   ------------------//

                Column {
                    Text(text = playlists.name)
                }

                Spacer(GlanceModifier.width(16.dp))


                //----------   Client Data and Town   ----------//

                Column {
                    Text(text =playlists.songCount.toString() , modifier = GlanceModifier.defaultWeight())
                }
            }


            /*------------------------------------------------
            |                 Action Buttons                 |
            ------------------------------------------------*/

            Row(horizontalAlignment = Alignment.CenterHorizontally, modifier = GlanceModifier.padding(start = 16.dp)) {

                //------------------   Call   ------------------//

                Image(
                    provider = ImageProvider(R.drawable.icono),
                    contentDescription = null,
                    modifier = GlanceModifier.size(18.dp).clickable(
                        actionStartActivity(Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", playlists.id, null)))
                    )
                )

                Spacer(GlanceModifier.width(16.dp))


            }
        }
    }



}