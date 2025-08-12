package com.tehseen.quicknotes.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.tehseen.quicknotes.ui.theme.screens.EditNoteScreen
import com.tehseen.quicknotes.ui.theme.screens.NotesListScreen
import com.tehseen.quicknotes.ui.theme.screens.SignInScreen
import com.tehseen.quicknotes.viewmodel.NoteViewModel

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val user = FirebaseAuth.getInstance().currentUser

    NavHost(
        navController = navController,
        startDestination = if (user == null) "signin" else "notes"
    ) {
        composable("signin") {
            SignInScreen(onSignedIn = {
                navController.navigate("notes") {
                    // This clears the back stack so the user can't navigate back to the sign-in screen
                    popUpTo("signin") { inclusive = true }
                }
            })
        }
        composable("notes") {
            val vm: NoteViewModel = viewModel()
            NotesListScreen(
                viewModel = vm,
                onEditNote = { id -> navController.navigate("edit/$id") },
                onCreateNote = { navController.navigate("edit/new") },
                onSignOutConfirmed = {
                    // same sign-out logic as before
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate("signin") {
                        popUpTo("notes") { inclusive = true }
                    }
                }
            )
        }
        composable("edit/{noteId}") { backStackEntry ->
            val noteIdArg = backStackEntry.arguments?.getString("noteId") ?: "new"
            val vm: NoteViewModel = viewModel()
            EditNoteScreen(
                viewModel = vm,
                noteId = noteIdArg,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
