[#ftl]
[#if student??]
{
student : {id : ${student.id}, name : "${(student.person.formatedName?js_string)!}"}
}
[/#if]