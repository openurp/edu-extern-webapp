[#ftl/]
[@b.head/]
[@b.toolbar title="校外考试报名信息"]
    bar.addBack("${b.text("action.back")}");
[/@]
[#assign labInfo]${b.text("ui.building.info")}[/#assign]
<table class="infoTable" width="100%">
    <tr>
        <td rowspan="4" class="title">
        <img src="avatar/user!show.action?user.name=${examSignup.std.user.code}"
            width="80px" height="110px" />
        <td rowspan="4" class="content">${examSignup.std.user.code}<br>
        ${examSignup.std.name}</td>
        <td class="title" width="15%">学期:</td>
        <td class="content" width="35%">${examSignup.semester.schoolYear}${examSignup.semester.name}</td>
    </tr>
    <tr>
        <td class="title" width="15%">报名费:</td>
        <td class="content" width="35%">${examSignup.feeOfSignup!}</td>
    </tr>
    <tr>
        <td class="title" width="15%">考纲费:</td>
        <td class="content" width="35%">${examSignup.feeOfOutline!}</td>
    </tr>
    <tr>
        <td class="title" width="15%">材料费:</td>
        <td class="content" width="35%">${examSignup.feeOfMaterial!}</td>
    </tr>
    <tr>
        <td class="title">院系:</td>
        <td class="content">${(examSignup.std.department.name)!}</td>
        <td class="title">考试科目:</td>
        <td class="content">${(examSignup.subject.category.name)!}[${(examSignup.subject.category.code)!}]</td>
    </tr>
    <tr>
        <td class="title">专业:</td>
        <td class="content">${(examSignup.std.major.name)!}</td>
        <td class="title">考试校区:</td>
        <td class="content">${(examSignup.campus.name)!}</td>
    </tr>
    <tr>
        <td class="title">报名时间:</td>
        <td class="content">${(examSignup.signupAt?string("yyyy-MM-dd HH:mm:ss"))!}</td>
        <td class="title">准考证号:</td>
        <td class="content">${(examSignup.examNo)!}</td>
    </tr>
</table>
[@b.foot/]