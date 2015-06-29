#Emotions on Azure HDInsight

# README FIRST
# 
# To get started
#	1. Login to Azure portal
#	2. Create storage using "Quick Create" option
#	3. Click on the newly created storage and navigate to "Containers"
#	4. Create a new container (it can be private)
#	5. Navigate to HDInsight and create a new Hadoop on Linux cluster
#	6. Ensure that both storage and cluster use the same Subscription
#	7. Edit the values in "#Edit values local" section.
#	8. Save the script and run it in Microsoft Azure PowerShell
#	9. Login using Microsoft account when the login prompt appears

#Edit values local
	$subscriptionName = "Free Trial"
	$clusterName = "emotionshd"
	$blobName = "emotionshd"
	$containerName = "emotionshd"
	$localJarFile = "D:\NUS\Assignments\SE-CLOUD\cloudca\EMOTIONS-0.0.1-SNAPSHOT.jar"
	$localDataFolder = "D:\NUS\Assignments\SE-CLOUD\cloudca\data\input"
#End edit values local


$destinationDataFolder = "/emotions/data/input"
$jarFile = "/emotions/jars/EMOTIONS-0.0.1-SNAPSHOT.jar"
$className = "edu.sg.nus.iss.cloudca.emotions.main.EmotionsAggregatorMain"
$statusDirectory = "/emotions/status"
$inputHappinessIndex = "/emotions/data/input/indico_range_happiness_index.dat"
	
Function RunEmotionsMapReduceJob ([string]$product)
{
	$outputDirectory = "/emotions/data/output/" + $product
	$inputDirectory = "/emotions/data/input/" + $product + ".txt"
	
	$emotions = New-AzureHDInsightMapReduceJobDefinition -JarFile $jarFile -ClassName $className -Arguments $inputDirectory, $outputDirectory, $inputHappinessIndex -StatusFolder $statusDirectory

	$emotionsJob = $emotions | Start-AzureHDInsightJob -Cluster $clusterName | Wait-AzureHDInsightJob

	Get-AzureHDInsightJobOutput -Cluster $clusterName -JobId $emotionsJob.JobId -StandardError
	Get-AzureHDInsightJobOutput -Cluster $clusterName -JobId $emotionsJob.JobId -StandardOutput
}

Function UploadDataFilesToBlobStore($localFolder, $destinationFolder, $context)
{
	$filesAll = Get-ChildItem $localFolder
	foreach ($file in $filesAll)
	{
		$fileName = "$localFolder\$file"
		$destinationName = "$destinationFolder/$file"
		Set-AzureStorageBlobContent -File $fileName -Container $containerName -Blob $destinationName -Context $context
	}
}

Function UploadJarFileToBlobStore($localFilePath, $destinationFilePath, $context)
{
	Set-AzureStorageBlobContent -File $localFilePath -Container $containerName -Blob $destinationFilePath -Context $context
}
Add-AzureAccount

Select-AzureSubscription $subscriptionName
$storageaccountkey = get-azurestoragekey $blobName | %{$_.Primary}
$destContext = New-AzureStorageContext -StorageAccountName $blobName -StorageAccountKey $storageaccountkey

UploadDataFilesToBlobStore $localDataFolder $destinationDataFolder $destContext
UploadJarFileToBlobStore $localJarFile $jarFile $destContext

RunEmotionsMapReduceJob "android"
RunEmotionsMapReduceJob "ios"
RunEmotionsMapReduceJob "iwatch"
RunEmotionsMapReduceJob "galaxys6"
RunEmotionsMapReduceJob "ps4"
RunEmotionsMapReduceJob "xbone"
RunEmotionsMapReduceJob "windows8"
RunEmotionsMapReduceJob "ubuntu"
RunEmotionsMapReduceJob "osx"


