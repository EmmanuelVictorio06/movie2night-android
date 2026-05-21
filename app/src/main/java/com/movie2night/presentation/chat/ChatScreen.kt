package com.movie2night.presentation.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.movie2night.domain.model.Message

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController,
    matchId: String,
    currentUserId: String,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(matchId) {
        viewModel.loadMessages(matchId)
    }

    // Rola para o final quando chega nova mensagem
    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chat") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Digite uma mensagem...") },
                    singleLine = true
                )
                Spacer(Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        viewModel.sendMessage(matchId, messageText)
                        messageText = ""
                    },
                    enabled = messageText.isNotBlank() && !uiState.isSending
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Enviar")
                }
            }
        }
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            items(uiState.messages) { message ->
                MessageBubble(
                    message = message,
                    isFromMe = message.senderId == currentUserId
                )
            }
        }
    }
}

@Composable
fun MessageBubble(message: Message, isFromMe: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isFromMe) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = if (isFromMe) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(
                text = message.content,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                color = if (isFromMe) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}