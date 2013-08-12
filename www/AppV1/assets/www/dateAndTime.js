    function populateLocalFields(id,localDate)
	{

	  if(id=='FromDateMap')
	    localStorage.setItem('fromLocalMapDate',localDate);
	  else if(id=='ToDateMap')
	    localStorage.setItem('toLocalMapTime',localDate);
	  else if(id=='FromDateAcc')
	    localStorage.setItem('fromLocalAccDate',localDate);
	  else if(id=='ToDateAcc')
	    localStorage.setItem('toLocalAccDate',localDate);
	  else if(id=='FromDateApp')
	    localStorage.setItem('fromLocalAppDate',localDate);
	  else if(id=='ToDateApp')
	    localStorage.setItem('toLocalAppDate',localDate);

	}

	  function getDate(elem)
	  {
            var myNewDate = new Date();
            // Same handling for iPhone and Android
            window.plugins.datePicker.show({
                date : myNewDate,
                mode : 'date', // date or time or blank for both
                allowOldDates : true
            }, function(returnDate) {
                var newDate = new Date(returnDate);
                var abc = newDate.toString();
                var localDate = newDate.getMonth()+1 +'/'+newDate.getDate()+'/'+newDate.getFullYear();
                populateLocalFields(elem.id,localDate);
                elem.value = abc.substring(0,4)+','+abc.substring(8,11)+abc.substring(4,8)+abc.substring(11,15);
                // This fixes the problem you mention at the bottom of this script with it not working a second/third time around, because it is in focus.
                elem.blur();
            });
        }

       function getTime(elem)
       {
          var time = elem.value;
          var myNewTime = new Date();

          myNewTime.setHours(time.substr(0, 2));
          myNewTime.setMinutes(time.substr(3, 2));

          // Same handling for iPhone and Android
          plugins.datePicker.show({
            date : myNewTime,
            mode : 'time', // date or time or blank for both
            allowOldDates : true
          }, function(returnDate) {
          // returnDate is generated by .toLocalString() in Java so it will be relative to the current time zone
            var newDate = new Date(returnDate);
            elem.value = newDate.toString("HH:mm").substr(16,8);
            elem.blur();
        });

       }