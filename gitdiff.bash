#!bash

# 定义输入参数
version1="e05273ee"
version2="c78d08ef"
output_dir="difference_folder"

# 确保输出目录存在
mkdir -p "$output_dir"

# 获取两个版本间的差异文件列表
diff_files=$(git diff --name-only "$version1" "$version2" )

# 遍历差异文件列表
for file in $diff_files; do
  # 获取差异类型（A: 添加，M: 修改，D: 删除）
#  diff_type=$(git diff --name-status "$version1" "$version2" | awk '{print $1}' | findstr "^$file$")
  file_path="$output_dir/$file"
#  file_path=$(echo "$file_path2" | tr '/' '\\')
#  //将file_path 按,分割 取最后一个
#  file_path2=$(echo "$file_path" | awk -F '/' '{print $NF}')
#  echo "file_path2 is $file_path2"

# echo "file_path2 is $file_path2"
#echo "diff_type is $diff_type"
#  case "$diff_type" in
#    A)  # 新增文件
#      echo "新增文件：$file,保存到:$file_path"
   folder=$(dirname "$file_path")
   echo "$folder"
mkdir -p "$folder"
      git show "$version1:$file" > "$file_path"
#      ;;
#    M)  # 修改文件
#       echo "修改文件：$file,保存到:$file_path"
      git show "$version2:$file" > "$file_path"
#      ;;
#    D)  # 删除文件
#      # 对于已删除的文件，你可以选择保留空文件或忽略（这里选择忽略）
#      ;;
#  esac
done
