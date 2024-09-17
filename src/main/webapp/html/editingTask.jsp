<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Task plan</title>
    <script src=https://code.jquery.com/jquery-3.6.0.min.js></script>
    <link href="/css/my.css" rel="stylesheet">
</head>
<body onload="showList(0)">
<h1>Task plan panel</h1>
<h2>List of tasks of ${ObjectUserName}</h2>
<label for="count_1">Count per page:</label>

<select name="count_1" id="count_1" onchange="showList(0)">
    <option value="3">3</option>
    <option value="5">5</option>
    <option value="10">10</option>
    <option value="20">20</option>
</select>

<table class="table_all" id="table_1">
    <tr>
       <th>#</th>
        <th>Name</th>
        <th>Title</th>
        <th>Status</th>
        <th>Expiration</th>
        <th>Starting</th>
        <th>Tags</th>
        <th>UserName</th>
        <th ${hideForm}>Edit</th>
        <th ${hideForm}>Delete</th>

            </tr>
        </table>

        <div id="paging_buttons">Pages:</div>

        <hr>
        <h2 ${hideForm} >Create new task:</h2>
        <div class="create" ${hideForm} >
            <div class="create__group">
                <label for="input_name_new">Name:</label>
                <input class="create__input" type="text" id="input_name_new" name="name" required maxlength="12">
            </div>
            <div class="create__group">
                <label for="input_title_new">Title:</label>
                <input class="create__input" type="text" id="input_title_new" title="name" required maxlength="30">
            </div>
            <div class="create__group">
                <label for="input_status_new">Status:</label>
                <select class="create__select" id="input_status_new" name='status'>

            <option value='NOT_STARTED'>NOT_STARTED</option>
            <option value='IN_PROGRESS'>IN_PROGRESS</option>
            <option value='DONE'>DONE</option>

        </select>
    </div>

    <div class="create__group">
        <label for="input_expiration_new">Expiration:</label>
        <input class="create__input" type="date" id="input_expiration_new" name="expiration" min="2000-01-01" max="3000-12-31">
    </div>
    <div class="create__group">
        <label for="input_starting_new">Start Day:</label>
        <input class="create__input" type="date" id="input_starting_new" name="creation" min="2000-01-01" max="3000-12-31">
    </div>
    <div class="create__group">
        <label for="input_tags_new">Tags:</label>
        <input class="create__input" type="text" id="input_tags_new" name="name" required maxlength="50">
    </div>
    <div>
        <label for="input_tags_new">UserName:</label>
        <input class="create__input" type="text" id="input_user_name_new" name="name" required maxlength="50">
    </div>

    <div class="create__group">
        <span>
            <button type="submit" onclick='createAcc()'>Create
                <img src="/img/save.png" alt="">
            </button>
        </span>
    </div>
</div>
<script>
    function showList(page_number) {
        let url = "/task" ;
        console.log("showList input page_number="+page_number);
        let countPerPage = $("#count_1").val();
        if (countPerPage === null) {
            countPerPage = 3;
        }
        url = url.concat("?pageSize=").concat(countPerPage);

        if (page_number !== null) {
            url = url.concat("&pageNumber=").concat(page_number);
        }

        $.get(url, function (response) {
            // remove all existing rows
            $("tr:has(td)").remove();

            $.each(response, function (i, item) {
                let tag_string="";
                item.tag.forEach((elem)=>tag_string+=elem.str+" ");
                let userNameLocal=null;
                if (item.user!=null){
                    let userN=item.user;
                    userNameLocal=userN.userName;
                }
                $('<tr>').html("<td>"
                    + item.task_id + "</td><td>"
                    + item.name + "</td><td>"
                    + item.title + "</td><td>"
                    + item.status + "</td><td>"

                    + new Date(item.endDate).toLocaleDateString() + "</td><td>"
                    + new Date(item.startDate).toLocaleDateString() + "</td><td>"
                    //+ item.banned + "</td><td>"
                     +   tag_string + "</td><td>"
                    + userNameLocal+ "</td><td>"
                    + "<button id='button_edit_" + item.task_id + "' ${hideForm} onclick='editAcc(" + item.task_id + ")'>" //было item.id
                    + "<img src='/img/edit.png' class='img_edit'>"
                    + "</button>" + "</td><td>"
                    + "<button id='button_delete_" + item.task_id + "' ${hideForm} onclick='deleteAcc(" + item.task_id + ")'>"
                    + "<img src='/img/delete.png' class='img_delete'>"
                    + "</button>" + "</td>")
                    .appendTo('#table_1');console.log("_____________");console.log("item_tag_="+item.tag["str"]);
            });
        });

        let totalCount = getTotalCount();
        let pagesCount = Math.ceil(totalCount / countPerPage);

        // remove all existing paging buttons
        $("button.pgn-bnt-styled").remove();

        //add paging buttons
        for (let i = 0; i < pagesCount; i++) {
            let button_tag = "<button>" + (i + 1) + ("</button>");
            let btn = $(button_tag)
                .attr('id', "paging_button_" + i)
                .attr('onclick', "showList(" + i + ")")
                .addClass('pgn-bnt-styled');
            $('#paging_buttons').append(btn);
        }

        // mak current page
        if (page_number !== null) {
            let identifier = "#paging_button_" + page_number;
            $(identifier).css("color", "red").css("font-weight", "bold");
        } else {
            $("#paging_button_0").css("color", "red").css("font-weight", "bold");
        }
        console.log("greaTinG from showACc");
    }

    function getTotalCount() {
        let value = 1;
        $.ajax({
            url: "/task/count",
            async: false,
            success: function (result) {
                value = result;
            }
        });
        return parseInt(value);
    }
    function deleteAcc(id) {
        let identifier_delete = "#button_delete_" + id;
        let current_tr_element = $(identifier_delete).parent().parent();
        let children = current_tr_element.children();


        let value_id = children[0].innerHTML;
        let value_name = children[1].innerHTML;
        let value_title = children[2].innerHTML;
        let value_status = children[3].innerHTML;
        let value_end_field = children[4].innerHTML;
        let value_end = new Date(value_end_field).getTime();

        let value_start_field = children[5].innerHTML;
        let value_start = new Date(value_start_field).getTime();
        let value_tag= children[6].innerHTML;

        let url = "/task";
        $.ajax({
            url: url,
            type: 'DELETE',
            dataType: 'json',
            contentType: 'application/json;charset=UTF-8',
            async: false,
            data: JSON.stringify({"task_id":value_id,"name": value_name, "title": value_title, "status": value_status, "endDate": value_end, "startDate": value_start,"tag":[{"str":value_tag}] }),
            success: function () {
                showList(getCurrentPage());
            }
        });
        showList(getCurrentPage());
    }

    function editAcc(id) {
        let identifier_edit = "#button_edit_" + id;
        let identifier_delete = "#button_delete_" + id;

        // lock delete button
        $(identifier_delete).remove();

        // replace "edit" button with "save"
        let save_image_tag = "<img src='/home/user/IdeaProjects/hibernate_additional/src/main/webapp/img/save.png' class='img_save'>";// /img.save.png
        $(identifier_edit).html(save_image_tag);
        let property_save_tag = "saveAcc(" + id + ")";
        $(identifier_edit).attr("onclick", property_save_tag);

        let current_tr_element = $(identifier_edit).parent().parent();
        let children = current_tr_element.children();

        let td_id = children[0];
        td_id.innerHTML = "<input id='input_id_" + id + "' type='text' value='" + td_id.innerHTML + "'>";

        let td_name = children[1];
        td_name.innerHTML = "<input id='input_name_" + id + "' type='text' value='" + td_name.innerHTML + "'>";

        let td_title = children[2];
        td_title.innerHTML = "<input id='input_title_" + id + "' type='text' value='" + td_title.innerHTML + "'>";

        let td_status = children[3];
        let status_id = "#select_status_" + id;
        let status_current_value = td_status.innerHTML;
        td_status.innerHTML = getDropdownStatusHtml(id);
        $(status_id).val(status_current_value).change();


        let td_end_date=children[4];
        const str4=td_end_date.innerHTML;
        let parts4 = str4.split("/");
        td_end_date.innerHTML= '<input className="create__input" type="date" id="input_end_change_'+id+ '" name="creation" min="2000-01-01" max="3000-12-31">';
        document.getElementById("input_end_change_"+id).valueAsDate = new Date(parseInt(parts4[2]),parseInt(parts4[0])-1,parseInt(parts4[1])+1);
        let td_start_date=children[5];
        const str5=td_start_date.innerHTML;
        let parts5 = str5.split("/");
        td_start_date.innerHTML= '<input className="create__input" type="date" id="input_start_change_'+id+ '" name="creation" min="2000-01-01" max="3000-12-31">';
        document.getElementById("input_start_change_"+id).valueAsDate = new Date(parseInt(parts5[2]),parseInt(parts5[0])-1,parseInt(parts5[1])+1);
        let td_tags = children[6];
        td_tags.innerHTML = "<input id='input_tag_" + id + "' type='text' value='" + td_tags.innerHTML + "'>";
    }

    function saveAcc(id) {
        let value_id = $("#input_id_"+id).val();
        let value_name = $("#input_name_" + id).val();
        let value_title = $("#input_title_" + id).val();
        let value_status = $("#select_status_" + id).val();
        let value_end_field = $("#input_end_change_" + id).val();
        let value_end = new Date(value_end_field).getTime();
        let value_start_field = $("#input_start_change_" + id).val();
        let value_start = new Date(value_start_field).getTime();
        let value_tag= $("#input_tag_" + id).val();


        let url = "/task";
        $.ajax({
            url: url,
            type: 'PUT',
            dataType: 'json',
            contentType: 'application/json;charset=UTF-8',
            async: false,
            data: JSON.stringify({"task_id":value_id,"name": value_name, "title": value_title, "status": value_status, "endDate": value_end, "startDate": value_start,"tag":[{"str":value_tag}] }),
            success: function () {
                showList(getCurrentPage());
            }
        });
        showList(getCurrentPage());
    }

    async function createAcc() {
        let value_name = $("#input_name_new").val();
        let value_title = $("#input_title_new").val();
        let value_status = $("#input_status_new").val();
        let expiration_date = $("#input_expiration_new").val();
        let datetime = new Date(expiration_date).getTime();
        let starting_date = $("#input_starting_new").val();
        let datetime_start = new Date(starting_date).getTime();
        let value_tags = $("#input_tags_new").val();
        let value_userName=$("#input_user_name_new").val();
        let current_page = window.location.href;

        let url = "/task";
        $.ajax({
            url: url,
            type: 'POST',
            dataType: 'json',
            contentType: 'application/json;charset=UTF-8',
            async: false,
            data:JSON.stringify({"name":value_name,"title":value_title,"endDate":expiration_date,"startDate":starting_date,"status":value_status,"tag":[{"str":value_tags}],"user":{"userName":value_userName}}),
            success: function () {
                $("#input_name_new").val("");
                $("#input_title_new").val("");
                $("#input_status_new").val();
                $("#input_expiration_new").val("");
                $("#input_starting_new").val("");
                $("#input_tags_new").val("");
                $("#input_user_name_new").val("");

                showList(getCurrentPage());
            }
        });
        console.log("Greeting from createAcc");
        return false;
    }

    function getCurrentPage() {
        let current_page = 1;
        $("button:parent(div)").each(function () {
            if ($(this).css("color") === "rgb(255, 0, 0)") {
                current_page = $(this).text();
            }
        })

        return parseInt(current_page) - 1;
    }

    function getDropdownStatusHtml(id) {
        let status_id = "select_status_" + id;
        return "<label for='status'></label>"
            + "<select id=" + status_id + " name='status'>"
             + "<option value='NOT_STARTED'>NOT_STARTED</option>"
            + "<option value='IN_PROGRESS'>IN_PROGRESS</option>"
            + "<option value='DONE'>DONE</option>"
            + "</select>";
    }

    function getDropdownProfessionHtml(id) {
        let profession_id = "select_profession_" + id;
        return "<label for='profession'></label>"
            + "<select id=" + profession_id + " name='profession'>"
            + "<option value='WARRIOR'>WARRIOR</option>"
            + "<option value='ROGUE'>ROGUE</option>"
            + "<option value='SORCERER'>SORCERER</option>"
            + "<option value='CLERIC'>CLERIC</option>"
            + "<option value='PALADIN'>PALADIN</option>"
            + "<option value='NAZGUL'>NAZGUL</option>"
            + "<option value='WARLOCK'>WARLOCK</option>"
            + "<option value='DRUID'>DRUID</option>"
            + "</select>";
    }

    function getDropdownBannedHtml(id) {
        let select_id = "select_banned_" + id;
        return "<label for='banned'></label>"
            + "<select id=" + select_id + " name='banned'>"
            + "<option value='false' >false</option>"
            + "<option value='true' >true</option>"
            + "</select>";
    }
</script>

</body>
</html>
