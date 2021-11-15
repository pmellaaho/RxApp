package net.pmellaaho.rxapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.material.composethemeadapter.MdcTheme
import net.pmellaaho.rxapp.model.Contributor

@Composable
fun ContributorsList(contributors: List<Contributor>) {
    LazyColumn(modifier = Modifier.semantics { contentDescription = "ContributorsList" }) {
        items(contributors) { contributor ->
            ContributorItem(contributor = contributor)
            Divider(startIndent = 16.dp)
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ContributorItem(
    contributor: Contributor,
    modifier: Modifier = Modifier
) {
    ListItem(
        modifier = modifier
            .clickable { /* todo */ }
            .padding(vertical = 8.dp),
        text = {
            Text(text = contributor.login!!)
        },
        secondaryText = {
            Text(text = contributor.contributions.toString())
        }
    )
}

@Preview("Contributor Item")
@Composable
private fun ContributorItemPreview() {
    MdcTheme {
        Surface {
            ContributorItem(
                contributor = Contributor(
                    login = "JakeWharton",
                    contributions = 1500
                )
            )
        }
    }
}