<html>
<head>
<script src="https://browserid.org/include.js" type="text/javascript"></script> 
<script src="http://code.jquery.com/jquery-1.7.2.js" type="text/javascript"></script>
<script type="text/javascript">
<!--
// This is the callback function called to verify assertion
function gotAssertion(assertion) {
  // got an assertion, now send it up to the server for verification
  if (assertion !== null) {
    $.ajax({
      type: 'POST',
      url: '/api/verify',
      data: { assertion: assertion },
      success: function(res, status, xhr) {
        	if (res === null) {
        		loggedOut();
        	} else {
        		loggedIn(res);
        	}
        },
      error: function(res, status, xhr) {
        alert("login failure" + res);
      }
    });
  } else {
    loggedOut();
  }
}

function loggedIn(res) {
	alert(">LoggedIn. " + res);
	window.location="/";
}

function loggedOut() {
	alert(">LoggedOut!");
}

function bID(){
    navigator.id.get(gotAssertion);
    return false;  
}
-->
</script>
</head>
<body>
<a href="#" id="browserid" title="Sign-in with BrowserID" onClick="bID();">  
  <img src="/images/sign_in_blue.png" alt="Sign in">
</a>
</body>
</html>