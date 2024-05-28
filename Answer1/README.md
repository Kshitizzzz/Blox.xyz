# 1.a What did the system do?

## File management service 
File Management Service is designed to offload server workload by leveraging AWS S3 and CloudFront. Here's how it works:
	1. Direct Uploads to S3: To reduce server load, we use pre-signed URLs, allowing clients to upload files directly to S3 buckets. This method grants temporary access to the bucket, enabling uploads without server intervention.

	2.Optimized File Access: For file retrieval, we provide CloudFront URLs. CloudFront caches files in its CDN network, reducing latency and improving download speeds, especially when the origin server is located in a different geographic region.

	3. Multipart Uploads: Our service supports multipart uploads. This feature is crucial for large files, ensuring that if an upload is interrupted, it can resume from where it left off, enhancing reliability and efficiency.
	
	4.Media File Transcoding: We incorporate media file transcoding, converting video files into the m3u8 format. This process compresses video files and improves streaming performance for multiple clients.

	5.When a client uploads a media file, an AWS Lambda function is triggered to detect the file type and initiate transcoding. During the transcoding process, the original file format is served to the client. To minimize polling, AWS SNS is used to notify the service once the transcoding is complete. A consumer in the service updates the status, allowing the service to then serve the optimized m3u8 file format.

## User Management Service
User management service is a centralized microservice designed to manage Docquity users' data efficiently and securely. Here’s an overview of its key features and benefits:

	1. Centralized User Data Management: Previously, each module at Docquity maintained its own user data, leading to inconsistencies when the same user registered across multiple modules. Our centralized service consolidates all user data in one place, eliminating data ambiguity and ensuring consistency.
	
	2. Enhanced Security with GPG Encryption: Given Docquity's role in connecting healthcare professionals with pharmacy companies, patients, and others in the healthcare market, securing personally identifiable information (PII) is paramount. We use GPG encryption to protect sensitive user data, including phone numbers, emails, medical documents, and licenses etc.

	3. User Consent for Data Sharing: Data is shared among multiple modules only with user consent, ensuring compliance with privacy regulations and maintaining user trust.

	4.Unique Identifiers for Data Retrieval: User details can be fetched using two parameters:
		- User Code: An internal alphanumeric string used by other services to fetch user details. This code is not exposed externally due to its simplicity and ease of generation.

		- UUID: Used for external access, ensuring secure and unique identification of users.

	5. Kafka Integration for Backward Compatibility: To support previous versions and ensure seamless data integration, user data is produced in Kafka.

	6. Scalability: We successfully migrated over 500k+ users to our service from different modules, demonstrating its capability to handle large-scale data management.

# 1.b. What other systems have you seen in the wild like that?

**Microsoft Azure Blob Storage**
Overview: Azure Blob Storage is a scalable object storage service for storing large amounts of unstructured data.
Features:

- Scalable and durable storage
- Integration with other Azure services like Functions and Event Grid
- Shared Access Signatures (SAS) for secure direct uploads
- Multipart upload support
- File versioning and lifecycle management
- Data encryption and access control
- Integration with Azure CDN for content delivery


**Okta**
Overview: Okta provides identity and access management services. It allows organizations to securely connect their employees, partners, and customers to the right technology.
Features:

- Centralized identity management
- Single Sign-On (SSO) across multiple applications
- Multi-factor authentication (MFA)
- User consent management and secure sharing of user data
- Integration with various enterprise applications and directories

# 1.c. How do you approach the development problem?

When addressing the development challenges for both the file management service **(FMS)** and the user management service **(UMS)**, a systematic approach was adopted:

1. Requirement Analysis:
	- Comprehensive analysis of project requirements for both services, considering functionalities, scalability, security, and performance.
	
	- Understanding the specific needs of each service, such as direct uploads to S3 for FMS and centralized user data management for UMS.

2. Architecture Design:
	- Designing scalable and reliable architectures for both services, ensuring they meet the respective functional and non-functional requirements.

	- Deciding on the use of cloud-native services, such as AWS S3, CloudFront, Lambda for FMS, and secure identity management systems for UMS.

3. Implementation Planning:
	- Planning the implementation of each service, including the identification of key components, data models, and integration points.

	- Considering factors like data encryption for sensitive information in UMS and efficient file storage and retrieval mechanisms for FMS.

4. Development:
	- Writing modular and maintainable code for implementing the planned architectures, adhering to coding standards and best practices.

	- Implementing features such as direct uploads to S3 with pre-signed URLs for FMS and centralized user data management with secure encryption for UMS.

5. Testing:
	- Conducting rigorous testing of both services to ensure functionality, security, and performance.

	- Performing unit tests, integration tests, and end-to-end tests to validate the behavior of each service under different scenarios.

6. Deployment:
	- Deploying the services to production environments, configuring them for optimal performance and scalability.

	- Implementing monitoring and logging solutions to track service health and performance metrics.

Throughout the development lifecycle, collaboration among team members, regular feedback loops, and continuous improvement were emphasized to address challenges effectively and deliver high-quality solutions. Additionally, adherence to industry best practices, documentation standards, and security protocols was maintained to ensure the reliability and integrity of both services.


# 1.d.  What were interesting aspects where you copied code from Stack Overflow?

Stack overflow is secondary as when the official documentation and resources did not provide the solution or when I need more clarity, stack overflow act as a valuable resource. The interesting aspects are :

1. Learning: Exposure to new techniques and best practices.
2. Optimization: Access to optimized and pre-tested code.
3. Broadening Knowledge: Discovering new libraries and tools.
4. Community Insight: Solutions vetted by experienced developers.
5. Problem-Solving: Efficient debugging and resolution of complex issues. 
6. Insight: Solutions vetted by experienced developers. 
7. Real-World Examples: Code examples often come with explanations and context, providing practical insights into how to implement a feature.
8. Comprehensive Explanations: Many solutions are accompanied by comprehensive explanations, which can help in understanding the underlying concepts.
9. Complex Issues: For complex issues that are hard to resolve, seeing how others have tackled similar problems can be incredibly insightful.
10. Feedback and Improvements: You can get immediate feedback and potential improvements on your code from a wide range of developers.


# 1.e.  What did you learn from some very specific copy paste? Mention explicitly some of them.

There was an instance when I faced difficulties and stack overflow played an invaluable role :

1. File Embedding : 

When embedding a file in my service. since It is often the case that a running Go program does not have access to the file system directories from which the the program was compiled. The embed feature slurps up files in the package source code tree at build time and makes those files available at runtime.
Package embed provides access to files embedded in the running Go program. There are 3 ways to embed a file: 
	- Embedding one file into a string.
	- Embedding one file into a slice of bytes.
	- Embedded one or more files into a file system.

2. Working with goroutines:
To achieve concurrency I used goroutines in a for loop but not getting the expected output. Since its not easy to debug the code when it is running in multiple goroutines but after debugging the code thoroughly I found a bug that goroutines works differently in the loop and found on stack overflow to how to handle this. And luckily GoLang fixed this bug in there newer version.

Here is an example :

package main
import (
	"fmt"
	"Sync"
)
func main() {
	var wg = &sync.WaitGroup{}
	for i := 0; i < 10; i++ {
		wg.Add(1)
		go func() {
			fmt.Println(i)
			wg.Done()
		}()
	}
	wg.Wait()
} 

In the Above code snippet the output we assumed is that we got numbers printed from 0-9 randomly. Right?
But no the output is 10 printing 10 times. This is because the for loop finishes before the goroutines schedule and execute.On the last execution when i is equal to 9 it must still follow the incremental rule i++ . Now that i is equal to 10, the conditional rule i < 10 results to false which is what stops the loop from executing the body further.And in Golang, the iterative variable is shared to the body as a pointer. So by passing variable i in the function the issue will be resolved. Below is the code snippet - 

var wg = &sync.WaitGroup{}
for i := 0; i < 10; i++ {
	wg.Add(1)
	go func(i int) {
		fmt.Println(i)
		wg.Done()
	}(i)
}
wg.Wait()
Now the output will be printing 0-9 values randomly.
