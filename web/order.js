function myReset()
{
	var form = document.getElementsByName("myForm")[0];	
	if (confirm("Do you want to reset the form? It will erase everything you wrote."))
	{
		form.reset();
	}
}
