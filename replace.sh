#!/bin/bash

# 定义要搜索和替换的字符串
read -p "请输入要搜索的凭据id: " search_string
read -p "请输入要替换凭据id: " replace_string

# 使用find命令递归查找所有文件，并对每个文件执行替换操作
echo "请执行命令:"
echo "find . -type f -exec sed -i "s/$replace_string/$replace_string/g" {} +"


# 定义要搜索和替换的字符串
read -p "请输入要搜索的jenkinslibrary repo名字: " search_string
read -p "请输入要替换的jenkinslibrary repo: " replace_string

echo "请执行命令:"
echo "find . -type f -exec sed -i 's|$search_string|$replace_string|g' {} +"

# 使用find命令递归查找所有文件，并对每个文件执行替换操作

