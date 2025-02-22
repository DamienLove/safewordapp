  FirebaseAuth.getInstance().signInWithCredential(credential)
      .addOnCompleteListener { task ->
          if (task.isSuccessful) {
              val user = task.result?.user
              // Successfully signed in
              // Add further logic, e.g., navigate to the main screen
          } else {
              // Handle error
              Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
          }
      }