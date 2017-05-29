[#ftl]
[#if student??]
{
student : {id : ${student.id}, name : "${(student.user.name?js_string)!}"}
}
[/#if]