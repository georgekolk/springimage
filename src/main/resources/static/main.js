$(document).ready(function () {

     $(".btn.btn-danger.btn-sm").click(function (event) {
            //stop submit the form, we will post it manually.
            event.preventDefault();
            //console.log(this.getAttribute('data-type'));
            var dataType = $(this).attr("data-type");
            var blog = $(this).attr("blog");

            console.log(dataType);
            $.ajax({
                    url: '/remove/' + blog + '/'+ dataType,
                    type: 'DELETE',
                    success: function(result) {
                        console.log("Lol_kek: ", dataType);
                        $( "div.gallery#" + dataType).hide( "slow",);

                    }
                });
        });

         $(".btn.btn-primary.btn-sm").click(function (event) {
                    //stop submit the form, we will post it manually.
                    event.preventDefault();
                    //console.log(this.getAttribute('data-type'));
                    var dataType = $(this).attr("data-type");
                    var blog = $(this).attr("blog");

                    console.log(dataType);
                    $.ajax({
                            url: '/approve/' + blog + '/'+ dataType,
                            type: 'DELETE',
                            success: function(result) {
                                console.log("Lol_kek: ", dataType);
                                $( "div.gallery#" + dataType).hide( "slow",);

                            }
                        });
                });
});
