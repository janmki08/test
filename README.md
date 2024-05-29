private bool isJumping = false;
private bool isGrounded = false;

void Update()
{
    CheckGround();
    Move();
    Jump();
}

void CheckGround()
{
    isGrounded = Physics2D.OverlapCircle(groundCheck.position, groundCheckRadius, whatIsGround);

    if (isGrounded)
    {
        isJumping = false;
        playerAnimator.SetBool("isJump", false);
    }
}

void Jump()
{
    if (isGrounded && Input.GetKeyDown(KeyCode.Space))
    {
        isJumping = true;
        rb.AddForce(Vector3.up * jumpForce, ForceMode2D.Impulse);
        JumpSound();
        playerAnimator.SetBool("isJump", true);
        print("점프!" + isJumping);
    }
}

private void OnCollisionEnter2D(Collision2D other)
{
    if (other.gameObject.CompareTag("Step") || other.gameObject.CompareTag("Ground"))
    {
        isJumping = false;
        playerAnimator.SetBool("isJump", false);
        print("착지!" + isJumping);
    }
}
