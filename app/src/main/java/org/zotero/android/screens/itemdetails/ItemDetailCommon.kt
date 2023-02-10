@file:OptIn(ExperimentalFoundationApi::class)

package org.zotero.android.screens.itemdetails

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import org.zotero.android.database.objects.Attachment
import org.zotero.android.architecture.ui.CustomLayoutSize
import org.zotero.android.helpers.formatter.dateFormatItemDetails
import org.zotero.android.sync.Note
import org.zotero.android.sync.Tag
import org.zotero.android.uicomponents.Drawables
import org.zotero.android.uicomponents.Strings
import org.zotero.android.uicomponents.foundation.safeClickable
import org.zotero.android.uicomponents.misc.CustomDivider
import org.zotero.android.uicomponents.theme.CustomPalette
import org.zotero.android.uicomponents.theme.CustomTheme
import java.util.Date

@Composable
internal fun FieldRow(
    detailTitle: String,
    detailValue: String,
    layoutType: CustomLayoutSize.LayoutType,
    textColor: Color = CustomTheme.colors.primaryContent,
    showDivider: Boolean,
    onDelete: (() -> Unit)? = null
) {
    Column {
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            if (onDelete != null) {
                Spacer(modifier = Modifier.width(8.dp))
                Image(
                    modifier = Modifier
                        .size(layoutType.calculateIconSize())
                        .safeClickable(
                            onClick = onDelete,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(bounded = false)
                        )
                        .padding(start = 4.dp),
                    painter = painterResource(id = Drawables.ic_delete_20dp),
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Column(modifier = Modifier.width(layoutType.calculateItemFieldLabelWidth())) {
                Text(
                    modifier = Modifier.align(Alignment.End),
                    text = detailTitle,
                    overflow =TextOverflow.Ellipsis,
                    maxLines = 1,
                    color = CustomTheme.colors.secondaryContent,
                    style = CustomTheme.typography.default,
                    fontSize = layoutType.calculateTextSize(),
                )
            }

            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(
                    modifier = Modifier,
                    text = detailValue,
                    color = textColor,
                    style = CustomTheme.typography.default,
                    fontSize = layoutType.calculateTextSize(),
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        if (showDivider) {
            CustomDivider()
        }
    }
}

@Composable
fun AddItemRow(
    titleRes: Int,
    onClick: () -> Unit,
    layoutType: CustomLayoutSize.LayoutType
) {
    Column(
        modifier = Modifier
            .safeClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(),
                onClick = onClick
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .padding(start = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier.size(layoutType.calculateIconSize()),
                painter = painterResource(id = Drawables.add_icon),
                contentDescription = null,
            )

            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp),
                text = stringResource(id = titleRes),
                fontSize = layoutType.calculateTextSize(),
                color = CustomTheme.colors.zoteroBlueWithDarkMode,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }

}

@Composable
fun DatesRows(
    dateAdded: Date,
    dateModified: Date,
    layoutType: CustomLayoutSize.LayoutType,
    showDivider: Boolean
) {
    FieldRow(
        detailTitle = stringResource(id = Strings.date_added),
        detailValue = dateFormatItemDetails.format(dateAdded),
        layoutType = layoutType,
        showDivider = showDivider,
    )
    FieldRow(
        stringResource(id = Strings.date_modified),
        dateFormatItemDetails.format(dateModified),
        layoutType,
        showDivider = showDivider,
    )
}

fun LazyListScope.notesTagsAndAttachmentsBlock(
    viewState: ItemDetailsViewState,
    layoutType: CustomLayoutSize.LayoutType,
    onNoteClicked: (Note) -> Unit,
    onNoteLongClicked: (Note) -> Unit = {},
    onAddNote: () -> Unit,
    onTagClicked: (Tag) -> Unit,
    onTagLongClicked: (Tag) -> Unit = {},
    onAddTag: () -> Unit,
    onAttachmentClicked: (Attachment) -> Unit = {},
    onAttachmentLongClicked: (Attachment) -> Unit = {},
    onAddAttachment: () -> Unit = {},
) {
    if (!viewState.data.isAttachment) {
        listOfItems(
            sectionTitle = Strings.notes,
            itemIcon = Drawables.item_note,
            itemTitles = viewState.notes.map { it.title },
            layoutType = layoutType,
            onItemClicked = {
                onNoteClicked(viewState.notes[it])
            },
            onItemLongClicked = {
                onNoteLongClicked(viewState.notes[it])
            },
            onAddItemClick = onAddNote,
            addTitleRes = Strings.add_note
        )
    }

    listOfItems(
        sectionTitle = Strings.tags,
        itemIcon = Drawables.ic_tag,
        itemTitles = viewState.tags.map { it.name },
        layoutType = layoutType,
        onItemClicked = {
            onTagClicked(viewState.tags[it])
        },
        onItemLongClicked = {
            onTagLongClicked(viewState.tags[it])
        },
        onAddItemClick = onAddTag,
        addTitleRes = Strings.add_tag
    )

    listOfItems(
        sectionTitle = Strings.attachments,
        itemIcon = Drawables.attachment_list_pdf,
        itemTitles = viewState.attachments.map { it.title },
        layoutType = layoutType,
        onItemClicked = {
            onAttachmentClicked(viewState.attachments[it])
        },
        onItemLongClicked = {
            onAttachmentLongClicked(viewState.attachments[it])
        },
        onAddItemClick = if (viewState.data.isAttachment) null else onAddAttachment,
        addTitleRes = Strings.add_attachment
    )
}

private fun LazyListScope.listOfItems(
    sectionTitle: Int,
    @DrawableRes itemIcon: Int,
    itemTitles: List<String>,
    onItemClicked: (Int) -> Unit,
    onItemLongClicked: (Int) -> Unit,
    @StringRes addTitleRes: Int,
    onAddItemClick: (() -> Unit)? = null,
    layoutType: CustomLayoutSize.LayoutType
) {
    itemDetailHeaderSection(sectionTitle, layoutType)
    itemsIndexed(
        itemTitles
    ) { index, item ->
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(),
                        onClick = { onItemClicked(index) },
                        onLongClick = { onItemLongClicked(index) }
                    )
                    .padding(all = 8.dp)
                    .padding(start = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = itemIcon),
                    modifier = Modifier.size(layoutType.calculateIconSize()),
                    contentDescription = null,
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp, top = 8.dp)
                ) {
                    Text(
                        text = HtmlCompat.fromHtml(
                            item,
                            HtmlCompat.FROM_HTML_MODE_LEGACY
                        ).toString(),
                        fontSize = layoutType.calculateTextSize(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    CustomDivider(modifier = Modifier.padding(top = 8.dp))
                }
            }
        }

    }
    if (onAddItemClick != null) {
        item {
            AddItemRow(
                layoutType = layoutType,
                titleRes = addTitleRes,
                onClick = onAddItemClick
            )
        }
    }

}

private fun LazyListScope.itemDetailHeaderSection(
    sectionTitle: Int,
    layoutType: CustomLayoutSize.LayoutType
) {
    item {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = CustomTheme.colors.zoteroItemDetailSectionBackground)
        ) {
            CustomDivider()
            Text(
                modifier = Modifier.padding(12.dp),
                text = stringResource(id = sectionTitle),
                color = CustomPalette.zoteroItemDetailSectionTitle,
                style = CustomTheme.typography.h6,
                fontSize = layoutType.calculateTextSize(),
            )
            CustomDivider()
        }
    }
}

