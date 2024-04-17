package com.example.das_app1.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
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
import com.example.das_app1.ui.theme.md_theme_light_background
import com.example.das_app1.widgets.WidgetReceiver.Companion.UPDATE_ACTION

class Widget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {

        provideContent {
            Content()
        }
    }

    @SuppressLint("StringFormatMatches")
    @Composable
    private fun Content() {

        val prefs = currentState<Preferences>()
        val context = LocalContext.current


        val user = prefs[currentUserKey]
        val data: String? = prefs[todayVisitsDataKey]
        val playlistList: List<CompactPlaylist> = if (data != null) Json.decodeFromString(data) else emptyList()


        Column(
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
            modifier = GlanceModifier
                .fillMaxSize()
                .background(color = md_theme_light_background)
                .padding(16.dp)
        ) {

            Text(
                text = if (user != null) context.getString(R.string.your_playlist, user) else " ",
                modifier = GlanceModifier.fillMaxWidth().padding(bottom = 16.dp),
                maxLines = 1,
                style =TextStyle(fontWeight = FontWeight.Medium, fontSize = 17.sp, textAlign = TextAlign.Center),
            )

            when {

                user == null -> {
                    Column(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = GlanceModifier.fillMaxSize().defaultWeight()
                    ) {
                        Text(text = context.getString(R.string.login_please))
                    }
                }

                playlistList.isEmpty() -> {
                    Column(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = GlanceModifier.fillMaxSize().defaultWeight()
                    ) {
                        Text(text = context.getString(R.string.create_your_first_playlist))
                    }
                }

                else -> {
                    LazyColumn(modifier = GlanceModifier.fillMaxSize().defaultWeight()) {
                        items(playlistList, itemId = { it.hashCode().toLong() }) { item ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = GlanceModifier.fillMaxWidth().padding(vertical = 8.dp)
                            ) {
                                Text(
                                    text = item.name,
                                    style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 16.sp, textAlign = TextAlign.Left),
                                    modifier = GlanceModifier.defaultWeight()
                                )

                                Spacer(modifier = GlanceModifier.width(16.dp))

                                Text(
                                    text = context.getString(R.string.songs, item.songCount),
                                    style = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp, textAlign = TextAlign.Center),
                                    modifier = GlanceModifier.defaultWeight()
                                )
                            }
                        }
                    }
                }
            }
            Spacer(GlanceModifier.height(8.dp))

            Image(
                provider = ImageProvider(R.drawable.reload),
                contentDescription = "Refresh",
                modifier = GlanceModifier.size(25.dp).clickable{ actionRunCallback<RefreshAction>()}
            )

        }
    }

     class RefreshAction : ActionCallback {
        override suspend fun onAction(
            context: Context,
            glanceId: GlanceId,
            parameters: ActionParameters
        ) {
            Widget().updateAll(context)
        }
    }

}

