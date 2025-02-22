val googleSignInClient = GoogleSignIn.getClient(this, gso)
val signInIntent = googleSignInClient.signInIntent
startActivityForResult(signInIntent, RC_SIGN_IN)

// Handling result
val credential = GoogleAuthProvider.getCredential(account.idToken, null)
FirebaseAuth.getInstance().signInWithCredential(credential){
    "users": {
        "userId": {
        "safeWords": ["word1", "word2"],
        "contacts": {
        "contact1": {"name": "John Doe", "phone": "1234567890"},
        "contact2": {"name": "Jane Doe", "phone": "0987654321"}
    },
        "settings": {
        "sensitivity": 0.8,
        "locationSharing": "always"
    }
    }
    }
}val dynamicLink = Firebase.dynamicLinks.shortLink(ShortLinkTaskOptions {
    domainUriPrefix = "https://yoursafewordapp.page.link"
    link(uri) // Uri pointing to app configuration
})    implementation 'com.google.firebase:firebase-auth:22.1.2'
    implementation 'com.google.android.gms:play-services-auth:20.8.0'